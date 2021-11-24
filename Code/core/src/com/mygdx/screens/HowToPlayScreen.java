package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.managers.Canvas;

public class HowToPlayScreen implements Screen{
	
	MultiplayerGame gm;
	private Sound bgm;
	private Canvas c;
	private MainMenuScreen mms;
	
	BitmapFont font;
	public HowToPlayScreen(MultiplayerGame game, Canvas c, MainMenuScreen mms) {
		gm = game;
		bgm = Gdx.audio.newSound(Gdx.files.internal("sound/fairyfountain.mp3"));
		this.c = c;
		this.mms = mms;
	}

	@Override
	public void show() {
		bgm.loop(0.3f);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 0);

		gm.batch.begin();
		
		c.drawH2PScreen();
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gm.setScreen(mms);
		}
		
		gm.batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		bgm.stop();
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
