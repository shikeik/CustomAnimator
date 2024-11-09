package com.goldsprite.customanimator.games;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.nio.file.Files;
import java.nio.file.*;
import java.util.*;
import com.goldsprite.customanimator.*;
import java.io.*;
import com.goldsprite.util.*;

/*
 使用Stage-Actor结构绘制帧动画
 */
public class Main1 implements IMain {

	private SpriteBatch batch;
	private String texsDir = "sk1", texsDir2 = "sk1WolfMan";
	private TextureRegion[] texs, texs2;
	private Animation<TextureRegion> anim, anim2;
	private float stateTime;
	private int viewWidth, viewHeight;
	private boolean runErr;
	private float keyDuration = 0.1f;


	public Main1() {
		try {
			launchGame();
		} catch (Exception e) {
			Log.logErr("游戏主循环发生异常: ", e);
		}
	}

	private void launchGame() {
		//初始化常量
		viewWidth = Gdx.graphics.getWidth();
		viewHeight = Gdx.graphics.getHeight();
		//实例化画笔
		batch = new SpriteBatch();

		//导入材质列表
		texs = getDirTextures(texsDir);
		texs2 = getDirTextures(texsDir2);

		//创建动画
		anim = new Animation<TextureRegion>(keyDuration, texs);
		anim2 = new Animation<TextureRegion>(keyDuration, texs2);
	}

	public void render() {
	    Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		try {
			if (runErr)
				return;

			renderGame();
		} catch (Exception e) {
			runErr = true;
			Log.logErr("渲染帧循环发生异常，已暂停渲染: ", e);
		}
	}

	private void renderGame() {
		batch.begin();
		{
			stateTime += Gdx.graphics.getDeltaTime();

			TextureRegion keyframe2 = anim2.getKeyFrame(stateTime, true);
			batch.draw(keyframe2, viewWidth / 2f+20, viewHeight / 2f+20);

			TextureRegion keyframe = anim.getKeyFrame(stateTime, true);
			batch.draw(keyframe, viewWidth / 2f, viewHeight / 2f);
		}
		batch.end();
	}


	public TextureRegion[] getDirTextures(String path) {
		List<TextureRegion> ret=new ArrayList<>();
		String str = "";
		try {
			FileHandle dir = Gdx.files.internal(path);
			List<FileHandle> files = Arrays.asList(dir.list());
			Collections.sort(files, 
				new Comparator<FileHandle>(){
					public int compare(FileHandle f, FileHandle f2) {
						return StringUtils.natureOrderCompare(f.name(), f2.name());
					}
				}
			);
			for (FileHandle file : files) {
				TextureRegion tex = new TextureRegion(new Texture(file));
				ret.add(tex);
				str += file.name() + "\n";
			}
		} catch (Exception e) {
			Log.logErr("获取目录材质组出错: ", e);
		}
		Log.log("anim" + str);
		return ret.toArray(new TextureRegion[]{});
	}

}
