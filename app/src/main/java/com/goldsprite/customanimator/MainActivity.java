package com.goldsprite.customanimator;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.goldsprite.customanimator.games.MyGame;
import android.widget.*;
import android.view.*;
 
public class MainActivity extends AndroidApplication {
	private LinearLayout gameLayout;
	
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		gameLayout = (LinearLayout) findViewById(R.id.gameLayout);
		
        AndroidApplicationConfiguration conf = new AndroidApplicationConfiguration();
		gameLayout.addView(initializeForView(new MyGame(),conf));
    }
	
} 
