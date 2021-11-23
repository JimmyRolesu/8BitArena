package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.managers.Canvas;
import com.mygdx.screens.HowToPlayScreen;
import com.mygdx.screens.MainMenuScreen;


public class MultiplayerGame extends Game {
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = (WIDTH/16) *9; //Forces 16/9 aspect ratio
	public static final int XSPLIT = 32;
	public static final int YSPLIT = 18;
	public static final int TILESIZE = WIDTH/XSPLIT;
	public static final int HUDSPACE = HEIGHT - TILESIZE*3;
	
	public SpriteBatch batch;
	public Canvas c;
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		c = new Canvas(this);
		
		this.setScreen(new MainMenuScreen(this, c));
		
	}

	@Override
	public void render () {
		super.render();
	}
	
}
