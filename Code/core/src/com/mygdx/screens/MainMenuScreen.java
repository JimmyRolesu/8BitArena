package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;
import com.mygdx.managers.Canvas;



public class MainMenuScreen implements Screen, TextInputListener{
	
	private static final int BUTTON_WIDTH = (MultiplayerGame.HEIGHT/96) * 20;
	private static final int BUTTON_HEIGHT = (MultiplayerGame.HEIGHT/176) * 20; 
	//private static final int START_Y = MultiplayerGame.HEIGHT/2;
	//private static final int H2P_Y = MultiplayerGame.HEIGHT/2 + (BUTTON_HEIGHT*2);
	
	private Sound bgm;
	private BitmapFont font;
	
	MultiplayerGame gm;
	private FreeTypeFontGenerator fontGen;
	private FreeTypeFontParameter fontParam;
	private String title;
	private GlyphLayout glyph;
	private float glyphW;
	private Boolean clickable;
	String name;
	
	
	Canvas canvas;

	public MainMenuScreen(MultiplayerGame game, Canvas c) {
		gm = game;
		canvas = c;
		bgm = Gdx.audio.newSound(Gdx.files.internal("sound/intro.mp3"));
		title = "8-Bit Arena";
		fontGen = new FreeTypeFontGenerator(Gdx.files.internal("eightbit.ttf"));
		fontParam = new FreeTypeFontParameter();
		fontParam.size = MultiplayerGame.TILESIZE;
		fontParam.color = Color.RED;
		font = fontGen.generateFont(fontParam);
		glyph = new GlyphLayout();
		glyph.setText(font, title);
		glyphW = glyph.width;	
		
		clickable = true;
		
		name = null; //Name of player when playing online
		
		
		
		
	}

	@Override
	public void show() {
		//bgm.loop(0.3f); //Will play the created sound and loop continuously

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 0);

		gm.batch.begin();
		
		
		
		//System.out.println(Gdx.input.getX() + ", " + Gdx.input.getY());
		//System.out.println(H2P_Y);
		//System.out.println(START_Y);
		//font.draw(gm.batch, "8-Bit Arena", 200, 200);
		
		
		buttons();
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
		// TODO Auto-generated method stub
		//bgm.stop();
		//dispose();

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
		gm.batch.dispose();
		fontGen.dispose();
		

	}
	
	private void buttons() {
		int x= MultiplayerGame.WIDTH/2 - BUTTON_WIDTH/2;
		int offset = MultiplayerGame.HEIGHT - BUTTON_HEIGHT/2;
		
		Texture playBtn, h2pBtn, playBtnSelected, h2pBtnSelected, exitBtn, exitBtnSelected, joinBtn, joinBtnSelected, hostBtn, hostBtnSelected;
		
		playBtn = new Texture("start.png");
		h2pBtn = new Texture("h2p.png");
		h2pBtnSelected = new Texture("h2p_selected.png");
		playBtnSelected = new Texture("start_selected.png");
		exitBtn = new Texture("exit.png");
		exitBtnSelected = new Texture("exit_selected.png");
		joinBtn = new Texture("join.png");
		joinBtnSelected = new Texture("join_selected.png");
		hostBtn = new Texture("host.png");
		hostBtnSelected = new Texture("host_selected.png");
		
		offset -= BUTTON_HEIGHT *1.2;
		
		//draw exit
		if (Gdx.input.getX() > x && Gdx.input.getX() < x + BUTTON_WIDTH && Gdx.input.getY() > offset -BUTTON_HEIGHT && Gdx.input.getY() < offset) {
			gm.batch.draw(exitBtnSelected, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
			if(Gdx.input.isTouched() && clickable) {
				clickable = false;
				//bgm.stop();
				Gdx.app.exit();
			}
		}
		else {
			gm.batch.draw(exitBtn, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
		}
		
		offset -= BUTTON_HEIGHT *1.2;
		
		//draw h2p
		if (Gdx.input.getX() > x && Gdx.input.getX() < x + BUTTON_WIDTH && Gdx.input.getY() > offset -BUTTON_HEIGHT && Gdx.input.getY() < offset) {
			gm.batch.draw(h2pBtnSelected, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
			if(Gdx.input.isTouched() && clickable) {
				clickable = false;
				//bgm.stop();
				gm.setScreen(new HowToPlayScreen(gm, canvas, this));
			}
		}
		else {
			gm.batch.draw(h2pBtn, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
		}
		
		offset -= BUTTON_HEIGHT *1.2;
		
		//draw join
		if (Gdx.input.getX() > x && Gdx.input.getX() < x + BUTTON_WIDTH && Gdx.input.getY() > offset -BUTTON_HEIGHT && Gdx.input.getY() < offset) {
			gm.batch.draw(joinBtnSelected, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
			if(Gdx.input.isTouched() && clickable) {
				clickable = false;
				Gdx.input.getTextInput(this, "Enter name", null, null);
				//bgm.stop();
				//gm.setScreen(new MainGameScreen(gm, canvas, this, false));//Sets client
			}
		}
		else {
			gm.batch.draw(joinBtn, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
		}
		
		offset -= BUTTON_HEIGHT *1.2;
		
		//draw host
		if (Gdx.input.getX() > x && Gdx.input.getX() < x + BUTTON_WIDTH && Gdx.input.getY() > offset -BUTTON_HEIGHT && Gdx.input.getY() < offset) {
				gm.batch.draw(hostBtnSelected, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
				if(Gdx.input.isTouched() && clickable) {
					clickable = false;
					//bgm.stop();
					gm.setScreen(new MainGameScreenServer(gm, canvas, this));//Sets host
				}
			}
		else {
				gm.batch.draw(hostBtn, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
		}
		
		offset -= BUTTON_HEIGHT *1.2;
				
				
//		if (Gdx.input.getX() > x && Gdx.input.getX() < x + BUTTON_WIDTH && Gdx.input.getY() > offset -BUTTON_HEIGHT && Gdx.input.getY() < offset) {
//			gm.batch.draw(playBtnSelected, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
//			if(Gdx.input.isTouched() && clickable) {
//				clickable = false;
//				bgm.stop();
//				//gm.setScreen(new MainGameScreen(gm, canvas, this));
//			}
//		}
//		else {
//			gm.batch.draw(playBtn, x, MultiplayerGame.HEIGHT - offset, BUTTON_WIDTH, BUTTON_HEIGHT);
//		}
//		
//		offset -= BUTTON_HEIGHT *1.2;
		
		font.draw(gm.batch, glyph, (MultiplayerGame.WIDTH-glyphW)/2, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE);
		
		
		
		
		
		
	}

	@Override
	public void input(String text) {
		clickable = true;
		this.name = text;
		
		if(name.length()>8) {
			name = name.substring(0, 8); //Players can only have a name with 8 letters or less.
		}
		
		gm.setScreen(new MainGameScreenClient(gm, canvas, this, name));//Sets client screen	
	}

	@Override
	public void canceled() {
		clickable = true; //Lets the user click again if they cancel out of button.
		
	}

	//gm.setScreen(new MainGameScreen(gm, canvas, this));

}
