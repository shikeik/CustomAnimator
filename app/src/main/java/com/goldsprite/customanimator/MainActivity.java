
package com.goldsprite.customanimator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.goldsprite.customanimator.games.MainGame;
import com.goldsprite.util.Files;
import com.goldsprite.util.Log;
import com.goldsprite.util.Project;
import android.os.Environment;
import android.widget.ScrollView;
import android.widget.ImageView;
import android.content.res.AssetManager;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.os.Handler;
import java.io.File;

public class MainActivity extends AndroidApplication {
	private static MainActivity instance;

	public static int PERMISSION_REQUEST_CODE = 100;

	private LinearLayout gameLayout;

	private View gameView;

	private MainGame mainGame;

	private Runnable requestSuccessRun = new Runnable(){
		public void run() {
			mainGame.init();
		}
	};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			initActivity();
		} catch (Exception e) {
			AppLog.toast("启动主活动代码异常: " + e.getMessage());
		}
    }
	
	//初始化主活动
	private void initActivity() {
		instance = this;
		AppLog.setCtx(this);
		AppLog.clearLog();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		gameLayout = (LinearLayout) findViewById(R.id.gameLayout);
        AndroidApplicationConfiguration conf = new AndroidApplicationConfiguration();
		gameLayout.addView(initializeForView(mainGame = new MainGame(), conf));

		requestAllPermission();
	}

	//请求文件权限
	public void requestAllPermission() {
		if (hasExternalStoragePermission()) {
			startGame();
		} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
			AppLog.dialog("申请存读权限", "应用需要此权限以维持日志系统运转，否则应用将无法正常调试.", 
				new Runnable(){
					public void run() {
						Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
						intent.setData(Uri.parse("package:" + getPackageName()));
						startActivityForResult(intent, PERMISSION_REQUEST_CODE);
					}
				}, 
				new Runnable(){public void run() {AppLog.finishWithToast("未获得授权，程序将退出.");}}
			);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
	}

	//请求权限回调结果
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		//如果请求代码不为此应用
		if (requestCode != PERMISSION_REQUEST_CODE) return;

		//返回结果数据长度>0且第一个为同意
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			AppLog.toast("存读权限申请通过");
			startGame();
			return;
		}

		//否则继续处理失败后
		//sdk23及以上
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//如果被拒绝(此方法首次申请以及普通被拒返回true，只有拒绝且不再询问时返回false)
			if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

				AppLog.dialog("存读权限申请失败", "应用需要此权限以维持日志系统运转，否则应用将无法正常调试.", 
					new Runnable(){public void run() {requestAllPermission();}}, 
					new Runnable(){public void run() {AppLog.finishWithToast("未获得授权，程序将退出.");}}
				);
				//用户拒绝且不再询问
			} else {
				//dialog弹窗引导
				permissionDialog();
			}
		}

	}

	//当从意图返回时
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//如果为权限申请
		if (requestCode == PERMISSION_REQUEST_CODE) {
			//还未授权
			if (!hasExternalStoragePermission()){
				AppLog.finishWithToast("未获得授权，程序将退出.");
			} else {
				AppLog.toast("已成功获得授权.");
				startGame();
			}
		}
	}

	public void startGame() {
		if (requestSuccessRun != null) 
			postRunnable(requestSuccessRun);
	}


	public void permissionDialog(){
		try {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			//设置不可取消
			b.setCancelable(false);
			b.setTitle("权限申请已被禁止，你需要手动设置");
			//设置引导内容布局
			{
				//声明布局
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.WRAP_CONTENT
				);
				LinearLayout ll = new LinearLayout(this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setLayoutParams(lp);
				//定义引导图片路径
				String pngsDir = "userHelper/";
				String[] pngsPath = getAssets().list(pngsDir);
				int i=1;
				//遍历添加引导文字与图片步骤
				for (String pngPath : pngsPath) {
					TextView tv = new TextView(this);
					tv.setText(String.format("第%d步", i++));
					tv.setTextColor(Color.BLACK);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					tv.setLayoutParams(lp);
					ll.addView(tv);

					InputStream is = getAssets().open(pngsDir + pngPath);
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					ImageView image = new ImageView(this);
					image.setImageBitmap(bitmap);
					image.setLayoutParams(lp);
					ll.addView(image);

					is.close();//释放流资源
				}
				ScrollView scroll = new ScrollView(this);
				scroll.addView(ll);
				b.setView(scroll);
			}
			b.setPositiveButton("确定", 
				new AlertDialog.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						// 跳转应用权限设置
						Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", getPackageName(), null);
						intent.setData(uri);
						startActivityForResult(intent, PERMISSION_REQUEST_CODE);
					}
				}
			);
			b.setNegativeButton("取消", 
				new AlertDialog.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						AppLog.finishWithToast("未获得授权，程序将退出.");
					}
				}
			);
			b.create().show();
		} catch (Exception e) {
			AppLog.dialog("权限申请dialog代码异常: ", e.getMessage());
		}
	}
	
	public boolean hasExternalStoragePermission() {
		boolean ret = true;
		try {
			String path = "/sdcard/" + System.currentTimeMillis() + ".txt";
			File file = new File(path);
			file.createNewFile();
			file.delete();
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

} 
