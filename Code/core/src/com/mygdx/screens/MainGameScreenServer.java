package com.mygdx.screens;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.entities.PlayerMP;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.HeldItem;
import com.mygdx.items.HeldItem01BowArrow;
import com.mygdx.items.HeldItem02Bomb;
import com.mygdx.items.HeldItem03Boomerang;
import com.mygdx.items.Item;
import com.mygdx.items.Item.ItemTypes;
import com.mygdx.level.Block;
import com.mygdx.level.Level;
import com.mygdx.level.LevelRandomiser;
import com.mygdx.level.MultiplayerLevel;
import com.mygdx.managers.Canvas;
import com.mygdx.net.GameServer;
import com.mygdx.net.Packet;
import com.mygdx.net.Packet01Disconnect;
import com.mygdx.net.Packet02Movement;
import com.mygdx.net.Packet03SendHp;
import com.mygdx.net.Packet05CheckServerConnection;
import com.mygdx.net.Packet06SendState;
import com.mygdx.net.Packet07AddItem;
import com.mygdx.net.Packet13SendPlayerItem;
import com.mygdx.net.Packet14AddHeldItem;
import com.mygdx.net.Packet15SetDead;
import com.mygdx.net.Packet16SendGameOver;
import com.mygdx.net.Packet17AntiCheatMove;
import com.mygdx.net.Packet21ChangeLevel;
import com.mygdx.net.Packet22ResetPlayer;
import com.mygdx.net.Packet26CheckClientConnection;
import com.mygdx.net.Packet27SendErrorMessage;

public class MainGameScreenServer extends MainGameScreen{
	
	private GameServer server;
	private MultiplayerLevel level;
	private int maxPlayers;
	private Boolean firstLoop;
	private List<Packet> packetQueue = new CopyOnWriteArrayList<Packet>();
	private String levelName;
	private LevelRandomiser levelRandomiser;
	private float sendPacketTimer;
	private float pingTimer;
	private int roundsToWin; //The number of rounds that must be won
	
	private List<String> messages;
	
	public MainGameScreenServer(MultiplayerGame game, Canvas c, MainMenuScreen mms) {
		super(game, c, mms);
		levelRandomiser = new LevelRandomiser();
		
		levelName = levelRandomiser.generateLevelName();
		
		level = new MultiplayerLevel(levelName);
		maxPlayers = 2;
		firstLoop = true;
		
		sendPacketTimer = 0;
		
		pingTimer = 0;
		
		roundsToWin = 3;
		
		messages = new CopyOnWriteArrayList<String>();
		messages.add("Server Started.");
		addToServerMessages("Now loading " + levelName.substring(0, levelName.length()-4) + "...");
		
		
		if(roundsToWin < 1) { //Make sure at least 1 round is won.
			roundsToWin = 1;
		}
	}

	public void show() {
		System.out.println("This is a server");
		server = new GameServer(this, maxPlayers); //Max number of players = 2
		server.start();
		
		
	}
	
	public void render(float delta) {
		runTime += Gdx.graphics.getDeltaTime(); // Increments by delta time each frame. This allows us to see the total run time of the program.
		// System.out.println(runTime);
		
		ScreenUtils.clear(0, 0, 0, 0); //Clears screen for next frame
		
		drawServer();
	}
	
	public void hide() {
		System.out.println("Closing");
		
		server.closeSocket(); // Closes the server socket so clients can no longer connect
		server.terminate(); //Closes the thread safely
	}
	
	public void addToServerMessages(String message) {
		messages.add(message);
	}
	
	private void removeListItems() {
		while(messages.size()>3) {
			messages.remove(0);
		}
	}
	
	/**
	 * Draws the server screen.
	 */
	private void drawServer() {
		gm.batch.begin();
		
		//System.out.println(server.connectedPlayers.size());
		
		if(server!=null) {
			canvas.drawServer(server.getPort(), server.getPlayerList(), maxPlayers, messages);
			removeListItems();
		
		//Loop that runs in the lobby
		if(!server.isGameRunning() && server.getNumConnectedPlayers() == maxPlayers) {
			server.sendStartPacket();
			messages.add("All players connected: Starting...");
			Packet21ChangeLevel packetLevel = new Packet21ChangeLevel(levelName);
			packetLevel.writeDataToClients(server);
			
			
		}
		
		
		//Loop that runs during gameplay
		if(server.isGameRunning()) {
			sendPacketTimer += Gdx.graphics.getDeltaTime();
			
			checkClientConnection(1f);
			
			if(firstLoop) {
				playerStartPositions();
				firstLoop = false;
			}
			
			level.randomItemTimer(server); //Can I spawn a new item into the level?
			level.checkPlayerCollisionsServer(server.getPlayerList()); //Checks collisions between other players
			level.checkItemCollision(server.connectedPlayers, server); //Is the player colliding with an item?
			level.doItemActions(); //Update item actions
			level.checkPlayerSwordCollisions(server.getPlayerList()); //Is a player sword hitting another?
			
			for(PlayerMP p : server.getPlayerList()) {
				level.checkCollision(p); //Checks collision against blocks
				level.checkHeldItemCollision(p);
				
				//System.out.println(p.getName() + "'s hp is: " +p.getHp());
			}
			
			for(PlayerMP p : server.connectedPlayers) {
				System.out.println(p.getName()+"'s state is: "+ p.getState());
				p.checkStun();
				//Packet13SendPlayerItem itemPacket = new Packet13SendPlayerItem(p.getName(),p.getCurrentItemId());
				//itemPacket.writeDataToClients(server);
				
				Packet03SendHp healthPacket = new Packet03SendHp(p.getName(), p.getMaxHp(), p.getHp());
				//packetQueue.add(healthPacket);
				healthPacket.writeDataToClients(server);
				
				Packet06SendState packetState = new Packet06SendState(p.getName(), p.getState().getId());
				packetState.writeDataToClients(server);
				
				Packet13SendPlayerItem packetItem = new Packet13SendPlayerItem(p.getName(), p.getCurrentItemId());
				packetItem.writeDataToClients(server);
				
				//System.out.println(p.getName()+"'s current item is: "+p.getCurrentItemId());
				
				Packet02Movement packetMove = new Packet02Movement(p.getName(), p.getX(), p.getY(), p.getDirection());
				//packetQueue.add(packetMove);
				packetMove.writeDataToClients(server);
			}
			
			for(Item i : level.getAddQueue()) {
				Packet07AddItem p = new Packet07AddItem(i.getUUIDasString(), i.getId(), i.getX(), i.getY());
				p.writeDataToClients(server);
				//packetQueue.add(p);
				
				level.removeFromAddQueue(i);
			}
			
			
			if (packetQueue.size() > 0 && sendPacketTimer >= (float)1/60) {
				for (Packet p : packetQueue) {
					p.writeDataToClients(server);
					packetQueue.remove(p);
				}
				sendPacketTimer = 0;

			}
			
			sendDeadPacket();
			checkRoundOver();
			isEveryoneHere();

		}
		
		}
		
		gm.batch.end();
	}
	
	/**
	 * Creates randomised start positions for the players at the start of the game.
	 */
	private void playerStartPositions(){
		for(PlayerMP p : server.getPlayerList()) {
			Boolean posOk = false;
			while(!posOk) {
				posOk = true;
				Random rd = new Random(); 
			
				float randomX = MultiplayerGame.TILESIZE + rd.nextFloat() * (MultiplayerGame.WIDTH-MultiplayerGame.TILESIZE*2);
				float randomY = MultiplayerGame.TILESIZE + rd.nextFloat() * (MultiplayerGame.HEIGHT-MultiplayerGame.TILESIZE*4);
			
				p.setX(randomX);
				p.setY(randomY);
				
				p.setOldX(randomX);
				p.setOldY(randomY);
				
				
				p.getHitbox().makeBox();
				
				for(Block b : level.getBlockList() ) {
					if(p.getHitbox().isColliding(b.getHitBox())&& b.isSolid()) {
						posOk = false;
						
					}
					//System.out.println("Can't spawn here");
				}
			}
			
			
			Packet02Movement packet = new Packet02Movement(p.getName(), p.getX(), p.getY(), p.getDirection());
			packet.writeDataToClients(server);
			
			
			
		}
	}
	
	public MultiplayerLevel getLevel() {
		return level;
	}
	
	private void sendDeadPacket() {
		for(PlayerMP player : server.getPlayerList()) {
			if(player.getHp() <=0) {
				player.setIsdead(true);
				//System.out.println(player.getName());
				Packet15SetDead packetDead = new Packet15SetDead(player.getName());
				packetDead.writeDataToClients(server);
			}
		}
	}
	
	private void checkRoundOver() {
		int numDeadPlayers = 0;
		String winner = "";
		for(PlayerMP p : server.getPlayerList()) {
			if(p.getIsDead()) {
				numDeadPlayers++;
			}
			else {
				winner = p.getName(); //There should only be 1 player who isn't dead and they will be the winner
			}
		}
		if(numDeadPlayers == maxPlayers - 1) {//If there is a winner
			
			levelName = levelRandomiser.generateLevelName(); //Load new random level
			level = new MultiplayerLevel(levelName); //Set the current to that random level
			
			addToServerMessages(winner + " won the round!"); //Add messages in server screen
			addToServerMessages("Now loading " + levelName.substring(0, levelName.length()-4) + "...");
			
			for(PlayerMP p : server.getPlayerList()) {
				
				p.reset(); //Reset player in server
				
				if(p.getName().equals(winner)) {
					p.roundWon(); //Update player "rounds won" counter
				}
				
				if(p.getRoundsWon() >= roundsToWin) {
					Packet16SendGameOver packetOver = new Packet16SendGameOver(winner);
					packetOver.writeDataToClients(server); //Lets clients know the game is won
					
					gm.setScreen(new MainGameScreenServer(gm, canvas, mms)); //Restarts the server for a new game
				}
				else {
					Packet22ResetPlayer packetReset = new Packet22ResetPlayer(p.getName());
					packetReset.writeDataToClients(server); //Resets player stats
				}
				
				
				
			}
			
			playerStartPositions(); //Reset Player positions
			
			Packet21ChangeLevel packetLevel = new Packet21ChangeLevel(levelName);
			packetLevel.writeDataToClients(server); //Makes client change level
			
			
			//Packet16SendGameOver packetOver = new Packet16SendGameOver(winner);
			//packetOver.writeDataToClients(server);
			
			
		}
	}

	public void calculatePlayerPosition(PlayerMP p, int direction, Packet17AntiCheatMove packet) {
		float tempY;
		float calculatedY;
		float tempX;
		float calculatedX;
		switch(direction) {
		default:
		case 0: //Down
			tempY = p.getY();
			calculatedY = tempY -= p.getSpeed()*Gdx.graphics.getDeltaTime();
			p.setY(calculatedY);
			p.setDirection(direction);
			p.updateWalkLoop();
			break;
		case 1: //Up
			tempY = p.getY();
			calculatedY = tempY += p.getSpeed()*Gdx.graphics.getDeltaTime();
			p.setY(calculatedY);
			p.setDirection(direction);
			p.updateWalkLoop();
			break;
		case 2: //Right
			tempX = p.getX();
			calculatedX = tempX += p.getSpeed()*Gdx.graphics.getDeltaTime();
			p.setX(calculatedX);
			p.setDirection(direction);
			p.updateWalkLoop();
			break;
		case 3: //Left
			tempX = p.getX();
			calculatedX = tempX -= p.getSpeed()*Gdx.graphics.getDeltaTime();
			p.setX(calculatedX);
			p.setDirection(direction);
			p.updateWalkLoop();
			break;			
		}
		p.getHitbox().makeBox();
		p.areaEdgeCheck();
		
		//This function is called out of a separate thread therefore, extra collision checks must be done to prevent unintended behaviour.
		level.checkPlayerCollisionsServer(server.getPlayerList()); 
		level.checkCollision(p);
		
		//packetQueue.add(packet);
		
		//level.checkPlayerCollisionsServer(server.getPlayerList());
		//System.out.println(p.getName() + " is at: " + p.getX() + "," + p.getY());
	}

	public void useItem(String name) {
		for (PlayerMP p : server.getPlayerList()) {
			if (p instanceof PlayerMP && ((PlayerMP) p).getName().trim().equals(name)) {																			
			
				ItemTypes type = Item.lookupItemType(p.getCurrentItemId());
				
				HeldItem i;
				Packet14AddHeldItem packet;
				
				switch (type) {
				default:
					break;
				case BOW:
					i = new HeldItem01BowArrow(p.getX(), p.getY(), p.getDirection(), p.getName());
					level.addHeldItem(i);
					packet = new Packet14AddHeldItem(i.getUUIDasString(), i.getId(), i.getX(), i.getY(), i .getOwner());
					packetQueue.add(packet);
					break;
				case BOMB:
					i = new HeldItem02Bomb(p.getX(),p.getY(),p.getName());
					level.addHeldItem(i);
					packet = new Packet14AddHeldItem(i.getUUIDasString(), i.getId(), i.getX(), i.getY(), i .getOwner());
					packetQueue.add(packet);
					break;
				case BOOMERANG:
					i = new HeldItem03Boomerang(p.getX(), p.getY(), p.getDirection(), p.getName());
					level.addHeldItem(i);
					packet = new Packet14AddHeldItem(i.getUUIDasString(), i.getId(), i.getX(), i.getY(), i .getOwner());
					packetQueue.add(packet);
					
					break;
				}
				
				break;// Stops looping once the player is found
			}
			
		}
	}
	
	/**
	 * Sends a "heartbeat" to every connected client and waits for them to respond. If they do not respond, the server will disconnect them.
	 * @param t
	 */
	private void checkClientConnection(float t) {
		
		pingTimer += Gdx.graphics.getDeltaTime();
		
		if (pingTimer > t) {//Ping to server every 2 seconds to see if it is alive.
			
			for(PlayerMP p : server.getPlayerList()) {
				
				if(p.getServerConnection()) {
					p.setFailedConnectionTimer(0); //Resets failed connection timer
				}
				
				p.setServerConnection(false); //Sets player to not connected
				
				Packet26CheckClientConnection packet = new Packet26CheckClientConnection(p.getName());
				server.sendData(packet.getData(), p.getIpAddress(), p.getPort()); //Try to get response from client
				
				if(!p.getServerConnection()) p.setFailedConnectionTimer(p.getFailedConnectionTimer()+1); //Increases failed connection counter if not connected
				if(p.getServerConnection()) p.setFailedConnectionTimer(0);
				
				if(p.getFailedConnectionTimer() > 2) { //If too many failed connections
					Packet01Disconnect packetDc = new Packet01Disconnect(p.getName());
					server.removeConnection(packetDc); //Remove player from server list and from other clients.
				}
				
				System.out.println(p.getName()+"'s failed connections are: "+p.getFailedConnectionTimer());
			}
			
			pingTimer = 0;
			//System.out.println(pingTimer);
		}
		//System.out.println(pingTimer);
		//System.out.println(failedConnectionCounter);
		
	}
	
	/**
	 * Checks if all the players are still in the game. If not, an error message is sent to remaining players and the server is reset for a new game.
	 */
	private void isEveryoneHere() {
		System.out.println(server.getPlayerList().size());
		if(server.getPlayerList().size() == maxPlayers - 1) {
			System.out.println("error");
			Packet27SendErrorMessage p = new Packet27SendErrorMessage("Someone left the game, now ending...");
			p.writeDataToClients(server);
			gm.setScreen(new MainGameScreenServer(gm, canvas, mms));
		}
	}
	

}
