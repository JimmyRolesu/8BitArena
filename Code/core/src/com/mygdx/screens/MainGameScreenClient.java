package com.mygdx.screens;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.entities.PlayerMP;
import com.mygdx.entities.PlayerStates;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.HeldItem;
import com.mygdx.items.HeldItem01BowArrow;
import com.mygdx.items.HeldItem02Bomb;
import com.mygdx.items.HeldItem03Boomerang;
import com.mygdx.items.HeldItem.HeldItemTypes;
import com.mygdx.items.Item;
import com.mygdx.items.Item01Heart;
import com.mygdx.items.Item02HeartContainer;
import com.mygdx.items.Item03Fairy;
import com.mygdx.items.Item04Clock;
import com.mygdx.items.Item05Bow;
import com.mygdx.items.Item06Bomb;
import com.mygdx.items.Item07Boomerang;
import com.mygdx.items.Item.ItemTypes;
import com.mygdx.level.Level;
import com.mygdx.level.MultiplayerLevel;
import com.mygdx.managers.Canvas;
import com.mygdx.net.GameClient;
import com.mygdx.net.Packet;
import com.mygdx.net.Packet00Login;
import com.mygdx.net.Packet01Disconnect;
import com.mygdx.net.Packet02Movement;
import com.mygdx.net.Packet03SendHp;
import com.mygdx.net.Packet04InitialServerPing;
import com.mygdx.net.Packet05CheckServerConnection;
import com.mygdx.net.Packet06SendState;
import com.mygdx.net.Packet08RemoveItem;
import com.mygdx.net.Packet09RequestLobbyInfo;
import com.mygdx.net.Packet11StartGame;
import com.mygdx.net.Packet14AddHeldItem;

public class MainGameScreenClient extends MainGameScreen implements TextInputListener{
	
	
	
	private Boolean connectedToServer, initialConnection, gameStarted, allPlayersInLobby, gameWon, localPadded, errorScreen;	
	private int failedConnectionCounter; //Counts how many times the server connection has failed.
	private float sendPacketTimer;
	private MultiplayerLevel level;
	private GameClient client;
	private int numConnectedPlayers, maxPlayers;
	
	private String pName;
	private String winner;
	private String errorMessage;
	private PlayerMP localP;
	
	private Boolean textInputOpen;
	
	private List<PlayerMP> connectedPlayers = new CopyOnWriteArrayList<PlayerMP>(); //Stores a list of all player entities currently connected
	private List<Packet> packetQueue = new CopyOnWriteArrayList<Packet>();

	public MainGameScreenClient(MultiplayerGame game, Canvas c, MainMenuScreen mms, String name) {
		super(game, c, mms);
		gameStarted = false;
		allPlayersInLobby = false;
		initialConnection = false; 	
		connectedToServer = false;
		gameWon = false;
		localPadded = false;
		textInputOpen = false;
		errorScreen = false;
		
		numConnectedPlayers = 0;
		maxPlayers = 0;
		
		pingTimer = 0f;
		returnTimer = 5f; //Timer to return to main screen if needed.
		sendPacketTimer = 0;
		failedConnectionCounter = 0;
		pName = name;
		//b = new Block01Standard(MultiplayerGame.TILESIZE/2, (MultiplayerGame.YSPLIT-2)*MultiplayerGame.TILESIZE-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);
		level = new MultiplayerLevel("level1.lvl");
		
		winner = "No one";
		errorMessage = "Unknown Error";
	}
	
	public void show() {
		System.out.println("This is a client");
		client = new GameClient(this, "localhost"); //192.168.1.10 server ip address //localhost for only this computer
		client.start();// Starts client thread
		
		//Packet04InitialServerPing pingPacket = new Packet04InitialServerPing();
		//pingPacket.writeDataToServer(client);
		
	}
	
	public void render(float delta) {
		
		
		//System.out.println(this.connectedPlayers.size());
		
		runTime += Gdx.graphics.getDeltaTime(); // Increments by delta time each frame. This allows us to see the total run time of the program.
		// System.out.println(runTime);
		
		sendPacketTimer += Gdx.graphics.getDeltaTime();
		
		if(client != null) {
			client.sendLatencyPacket();
		}
			
		ScreenUtils.clear(0, 0, 0, 0); //Clears screen for next frame
		
		
		if(errorScreen) {
			this.drawErrorScreen();
		}
		
		else if(!initialConnection) {
			this.drawNoInitialConnection();
		}
		
		//Draw the game
		else if (initialConnection && failedConnectionCounter < 10 && gameStarted && !gameWon) {//Only draw game and players if currently connected to server
			//System.out.println("Connected!");
			drawClient();//If there is no connection to the server for set time, stop client game.
			
			if(!textInputOpen && Gdx.input.isKeyJustPressed(Keys.T)) {
				textInputOpen = true;
				Gdx.input.getTextInput(this, "Enter message: ", null, null);
			}
			
			checkServerConnection(0.2f); //Sends a pulse every 0.2 seconds
			if(failedConnectionCounter > 1) System.out.println("Packet Lost!");


			//if(runTime>5) Runtime.getRuntime().halt(0); //Used to simulate disconnection to server without being able to send a packet.

		}
		
		//Draw the lobby
		else if(initialConnection && failedConnectionCounter < 10 && !gameStarted && !gameWon) {
			this.drawLobby();
			Packet09RequestLobbyInfo pLobby = new Packet09RequestLobbyInfo();
			pLobby.writeDataToServer(client);
			
		}

		else if(initialConnection && failedConnectionCounter >= 10) {//Stops the game if there have been too many lost packets
			System.out.println("Lost Connection to Server!");
			this.drawLostConnectionToServer(returnTimer);
		}
		
		else if(gameWon) {
			this.drawGameWonScreen();
		}
	}
	
	public void hide() { //Disconnects the player from the server if they close the game or if 
		System.out.println("Closing");
			if(connectedToServer) {//Only Send DC packet if connected to server
				Packet01Disconnect packet = new Packet01Disconnect(localP.getName());
				packet.writeDataToServer(client);
			}
			
			client.closeSocket();
	}
	
	
	private void drawErrorScreen() {
		gm.batch.begin();
		
		if(client.isRunning()) {
			client.terminate();
			client.closeSocket();
		}
		
		
		canvas.drawErrorScreen(errorMessage);
		
		if(Gdx.input.isKeyPressed(Keys.ENTER)) {
			gm.setScreen(mms);
		}
		
		gm.batch.end();
		
	}

	private void drawGameWonScreen() {
		gm.batch.begin();
	
		if(client.isRunning()) {
			client.terminate();
			client.closeSocket();
		}
		
		canvas.drawGameOverScreen(winner);
		
		if(Gdx.input.isKeyPressed(Keys.ENTER)) {
			gm.setScreen(mms);
		}
		
		gm.batch.end();
		
	}

	private void drawLobby() {
		gm.batch.begin();
		canvas.drawLobby(numConnectedPlayers, maxPlayers);
		gm.batch.end();
		
	}

	private void drawClient() {
		gm.batch.begin();
		canvas.drawFloor();
		
		//Level is drawn and any logic is performed
		level.drawLevel(canvas);
		level.updateBlockFrames();
		level.doItemActions();
		
		for (PlayerMP p : connectedPlayers) {//Draw each player and calculate any logic needed.	
			
			if (p.getLocalCheck()) { //These only occur for the local player
				
				p.setHp(p.getMaxHp()); //Trying to cheat
				
				p.updateXY(packetQueue); //Check key input
				p.updateState(packetQueue); //Check state change (eg sword attacks)
				
				p.checkItemUseOnline(packetQueue); //Check if an item is being used
				
				canvas.drawHUD(localP.getHp(), localP.getMaxHp(), localP.getName(), connectedToServer); //Draw the HUD
				
				//Queue packets in "packetQueue" and send them one after another here. Timer can adjust sending rate.
				if (sendPacketTimer >= 0) {				
					if(packetQueue.size() > 0) {
						for (Packet packet : packetQueue) {
						  packet.writeDataToServer(client);
						  packetQueue.remove(packet);
						}
					}	
					sendPacketTimer = 0;
				}
				
				
			}

			//p.deadReckon(); //Used to smooth enemy movements

			
			p.loopTimer(canvas); //Loop player movement frames
			p.areaEdgeCheck(); //Check edge collision
			p.drawPlayer(canvas);//Draw the player sprite
			
			//System.out.println(p.getName()+"'s current item is: "+p.getCurrentItemId());

			//System.out.println(connectedPlayers.size());
			//System.out.println(p.getState());
		}
		
		
		for(PlayerMP p :connectedPlayers) {//Separate loop to draw player names. (This makes sure that names are drawn above any other sprites
			p.drawPlayerName(canvas, p.showHp(), p.localCheck());
			p.drawMessage(canvas);
			p.clearMessage();
		}
		
		gm.batch.end();
		
	}
	
	/**
	 * Draws what the client sees if there is no online server
	 */
	private void drawNoInitialConnection() {
		gm.batch.begin();
		
		canvas.drawNoServerFound();
		
		if(client != null && pName != null) {
			Packet04InitialServerPing pingPacket = new Packet04InitialServerPing(pName);
			pingPacket.writeDataToServer(client);
		}
		gm.batch.end();
	}

	public void searchForAvailableServer() {
		
	}

	/**
	 * Draws what appears when connection is lost to the server
	 * @param timer
	 */
	private void drawLostConnectionToServer(float timer) {
		gm.batch.begin();
		this.returnTimer = timer - Gdx.graphics.getDeltaTime();
		System.out.println(timer);
		canvas.drawHUD(localP.getHp(), localP.getMaxHp(), localP.getName(), connectedToServer);
		canvas.drawConnectionLost(timer);
		gm.batch.end();
		if (timer <= 0f) gm.setScreen(mms);
	}
	
	/**
	 * Sends a "heartbeat" to the server every t seconds to make sure that there is still a connection between it and the client.
	 * @param t - The "pulse", sent every t seconds
	 */
	private void checkServerConnection(float t) {
		
		pingTimer += Gdx.graphics.getDeltaTime();
		
		if (pingTimer > t) {//Ping to server every 2 seconds to see if it is alive.
			if(connectedToServer) failedConnectionCounter = 0; //Counter reset if server is connected to
			connectedToServer = false;
			
			Packet05CheckServerConnection checkPacket = new Packet05CheckServerConnection();//Send ping
			checkPacket.writeDataToServer(client);
			
			if(!connectedToServer) failedConnectionCounter++;
			if(connectedToServer) failedConnectionCounter = 0;
			
			pingTimer = 0;
			//System.out.println(pingTimer);
		}
		//System.out.println(pingTimer);
		//System.out.println(failedConnectionCounter);
		
	}
	
	/**
	 * Sets the initial connection which says that the client has connected to the server.
	 */
	public void setInitialConnection() {
		initialConnection = true;
		
	}

	/**
	 * Used to manually set the connection status of the client to the server
	 * @param connection
	 */
	public void setConnectedToServer(Boolean connection) {
		this.connectedToServer = connection;	
	}
	

	/**
	 * Adds a multiplayer player object to the connected player list
	 * @param p
	 */
	public void addPlayer(PlayerMP p) {
		connectedPlayers.add(p);
	}

	/**
	 * Removes multiplayer player object from connected player list
	 * @param name
	 */
	public void removePlayer(String name) {
		// int index = 0;
		//PlayerMP tempPlayer = null;
		for (PlayerMP p : connectedPlayers) {
			if (p instanceof PlayerMP && ((PlayerMP) p).getName().trim().equals(name)) {// Makes sure that the object is
																						// a player mp and not a regular
				this.connectedPlayers.remove(p);
				break;// Stops looping once the player is found
			}
			// index++;
		}
		//this.connectedPlayers.remove(tempPlayer);

	}
	
	/**
	 * Sets the position of a selected player. Used to move other connected players but can also function as a teleport.
	 * @param name
	 * @param x
	 * @param y
	 * @param direction
	 * @param stateInt 
	 */
	public void movePlayer(String name, float x, float y, int direction) {
		//PlayerMP tempPlayer = null;
		for (PlayerMP p : connectedPlayers) {
			if (p instanceof PlayerMP && ((PlayerMP) p).getName().trim().equals(name)) {// Makes sure that the object is player mp
																					
				//System.out.println(p.getState());
				if(p.getX() == x && p.getY() == y) {
					
				}
				else {
					p.updateWalkLoop();
					
					p.setX(x);
					p.setY(y);
					p.setDirection(direction);
					p.areaEdgeCheck();
					p.getHitbox().makeBox();
					//p.setState(p.lookupState(stateInt));
					
				}
				
				if(!p.localCheck()) {
					p.updatePreviousCoordinateLists(x, y);
				}
				
				
				
				break;// Stops looping once the player is found
			}
			// index++;
		}
		//tempPlayer.x = x;
		//tempPlayer.y = y;
	}
	
	/**
	 * Sets the hp value of the specified player
	 * @param name
	 * @param maxHp
	 * @param hp
	 */
	public void setHp(String name, int maxHp, int hp) {
		for (PlayerMP p : connectedPlayers) {
			if (p instanceof PlayerMP && ((PlayerMP) p).getName().trim().equals(name)) {// Makes sure that the object is
																						// a player mp and not a regular
																						// player
				p.setMaxHp(maxHp);
				p.setHp(hp);
				
				break;// Stops looping once the player is found
			}
			// index++;
		}
	}
	
	/**
	 * Sets the state for players based on the state id value.
	 * @param name
	 * @param stateInt
	 */
	public void setState(String name, int stateInt) {
		for (PlayerMP p : connectedPlayers) {
			if (p instanceof PlayerMP && ((PlayerMP) p).getName().trim().equals(name)) {// Makes sure that the object is
																						// a player mp and not a regular
																						// player
				p.setState(p.lookupState(stateInt));
				
				break;// Stops looping once the player is found
			}
		}
	}
	
	/**
	 * Connects the local player (the one controlled by the local client).
	 */
	public void connectLocalPlayer() {
		if(connectedToServer && !localPadded) {
			localP = new PlayerMP(200, 100, pName, null, -1, true, client);
			connectedPlayers.add(localP);
			Packet00Login loginPacket = new Packet00Login(localP.getName(), localP.getX(), localP.getY());//Creates login packet 
			loginPacket.writeDataToServer(client);// Sends the login packet data to the server to add the new player
			localPadded = true;
		}		
	}
	
	public GameClient getClient() {
		return client;
	}
	
	/**
	 * Print a list of all currently added players. Used for debug.
	 */
	public void printPlayerList() {
		for (PlayerMP p : connectedPlayers) {
			System.out.println(p.getName());
		}
	}

	public void addItem(String uuid, int itemId, float x, float y) {
		Item item = null;
		ItemTypes type = Item.lookupItemType(itemId);
		
		switch(type) {
		default:
		case HEART:
			item = new Item01Heart(x,y,uuid);
			break;
		case HEARTCONTAINER:
			item = new Item02HeartContainer(x,y,uuid);
			break;
		case FAIRY:
			item = new Item03Fairy(x,y,uuid);
			break;
		case CLOCK:
			item = new Item04Clock(x,y,uuid);
			break;
		case BOW:
			item = new Item05Bow(x,y,uuid);
			break;
		case BOMB:
			item = new Item06Bomb(x,y,uuid);
			break;
		case BOOMERANG: 
			item = new Item07Boomerang(x,y,uuid);
			break;
		
	}
		level.addItem(item);
	}

	public void removeItem(String uuid) {
		for(Item item: level.getItemList()) {
			if(item.getUUIDasString().equals(uuid)) {
				level.getItemList().remove(item);
			}
		}
		
		
	}

	public void updateLobbyInfo(int numConnectedPlayers, int maxPlayers) {
		this.numConnectedPlayers = numConnectedPlayers;
		this.maxPlayers = maxPlayers;
		
	}

	public void startGame() {
		gameStarted = true;
		Packet11StartGame p = new Packet11StartGame();
		p.writeDataToServer(client);
		
	}

	public void updatePlayerItems(String name, int itemId) {
		for (PlayerMP p : connectedPlayers) {
			if (p instanceof PlayerMP && ((PlayerMP) p).getName().trim().equals(name)) {// Makes sure that the object is
																						// a player mp and not a regular
																						// player
				p.setCurrentItem(itemId);
				
				break;// Stops looping once the player is found
			}
		}
		
	}

	public void addHeldItems(String owner, float x, float y, String uuid, int itemId) {
		for(PlayerMP p : connectedPlayers) {
			if(p instanceof PlayerMP && ((PlayerMP)p).getName().trim().equals(owner)) {
				HeldItemTypes type = HeldItem.lookupHeldItemType(itemId);
				switch(type) {
				default:
				case BOWARROW:
					level.getHeldItemList().add(new HeldItem01BowArrow(x, y, p.getDirection(), uuid, owner));
					break;
				case BOMB:
					level.getHeldItemList().add(new HeldItem02Bomb(x,y,owner));
					break;
				case BOOMERANG:
					level.getHeldItemList().add(new HeldItem03Boomerang(x, y, p.getDirection(), uuid, owner));
				}
			break;
			}
		}
		
	}

	public void setDeadPlayer(String usrName) {
		for(PlayerMP p : connectedPlayers) {
			if(p instanceof PlayerMP && ((PlayerMP)p).getName().trim().equals(usrName)) {
				p.setIsdead(true);
				System.out.println(p.getName() + " is dead? " + p.getIsDead());
				break;
			}
		}
		
	}

	public void setGameOver(String winner) {
		this.winner = winner;
		gameWon = true;
		
	}

	public void changeLevel(String levelName) {
		level = new MultiplayerLevel(levelName);	
	}

	public void resetPlayer(String name) {
		for(PlayerMP p : connectedPlayers) {
			if(p instanceof PlayerMP && ((PlayerMP)p).getName().trim().equals(name)) {
				p.reset();
				break;
			}
		}
		
		
	}

	public void movePlayerInterpolated(String name, int direction, long timeDone) {
		for(PlayerMP p : connectedPlayers) {
			if(p instanceof PlayerMP && ((PlayerMP)p).getName().trim().equals(name) && !(p.localCheck())) {
				Date now = new Date();
				long tempTime = now.getTime() - timeDone;
				System.out.println(tempTime);
				
				break;
			}
		}
		
	}

	@Override
	public void input(String text) {
		textInputOpen = false;
		//System.out.println(text);
		String t = text;
		
		if(t.length()>30) {
			t = t.substring(0, 30) +"...";
		}
		
		for(PlayerMP p : connectedPlayers) {
			if(p.localCheck()) {
				p.setMessage(packetQueue, t);
			}
		}
		
	}

	@Override
	public void canceled() {
		textInputOpen = false;
		
	}

	public void setMessage(String name, String message) {
		for(PlayerMP p : connectedPlayers) {
			if(p instanceof PlayerMP && ((PlayerMP)p).getName().trim().equals(name)) {
				p.setMessage(message);
				break;
			}
		}
		
		
	}

	public void setErrorScreen(String message) {
		errorScreen = true;
		errorMessage = message;
		
	}


}
