package com.mygdx.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.entities.PlayerStates;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Block;
import com.mygdx.level.Block01Standard;
import com.mygdx.level.Level;
import com.mygdx.level.MultiplayerLevel;
import com.mygdx.managers.Canvas;
import com.mygdx.net.GameClient;
import com.mygdx.net.GameServer;
import com.mygdx.net.Packet00Login;
import com.mygdx.net.Packet01Disconnect;
import com.mygdx.net.Packet03SendHp;
import com.mygdx.net.Packet04InitialServerPing;
import com.mygdx.net.Packet05CheckServerConnection;

public class MainGameScreen implements Screen {

	public static final float SPEED = 100;
	private MainGameScreen mainGameScreen;
	
	//Player p, p2;
	protected Canvas canvas; //Canvas object responsible for drawing to the screen
	float runTime, pingTimer, returnTimer;// run time shows how long the program has been running for. ping timer is used to time intervals pings are sent to server.

	protected MultiplayerGame gm; //Various screens so user can return to them
	protected MainMenuScreen mms; 

	//Queue<PlayerMP> connectedPlayers = new ConcurrentLinkedQueue<PlayerMP>(); // No longer stored in an ArrayList since they are not thread safe.
	

	
	//private Block b;
	

	
	/**
	 * Main Game Screen if one is running either a server or client
	 * @param game
	 * @param c
	 * @param mms
	 * @param serverCheck
	 * @param name
	 */
	public MainGameScreen(MultiplayerGame game, Canvas c, MainMenuScreen mms) {
		
		gm = game;
		this.mms = mms;// Allows us to return to the main menu
		canvas = c; //Use the same canvas we created previously
		
		mainGameScreen = this;// Creates an instance of this screen for use elsewhere
		
		
		
		
	}
	
	
	@Override
	public void show() { //Occurs when the screen is first loaded.
		
		
	}

	@Override
	public void render(float delta) {//Code here occurs every frame
		
		runTime += Gdx.graphics.getDeltaTime(); // Increments by delta time each frame. This allows us to see the total run time of the program.
		// System.out.println(runTime);
		
		ScreenUtils.clear(0, 0, 0, 0); //Clears screen for next frame
		
		

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		System.out.println("Closing");

	}

	@Override
	public void dispose() {

	}

}
