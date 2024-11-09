package com.goldsprite.customanimator.games;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.*;

/*

*/
public class MainGame extends ApplicationAdapter{
	IMain main;
	boolean initialized = false;

	
	@Override
	public void create() {
	}
	
	public void init(){
		main = new Main1();
		initialized = true;
	}

	@Override
	public void render() {
		if(!initialized) return;
		
		main.render();
	}
	
}
