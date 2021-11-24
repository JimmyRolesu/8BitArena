package com.mygdx.entities;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.mygdx.items.HeldItem01BowArrow;
import com.mygdx.items.HeldItem02Bomb;
import com.mygdx.items.Item;
import com.mygdx.items.Item.ItemTypes;
import com.mygdx.managers.Canvas;
import com.mygdx.net.GameClient;
import com.mygdx.net.Packet;
import com.mygdx.net.Packet02Movement;
import com.mygdx.net.Packet17AntiCheatMove;
import com.mygdx.net.Packet18AntiCheatUseItem;
import com.mygdx.net.Packet19AntiCheatUseSword;
import com.mygdx.net.Packet20AntiCheatNoSword;
import com.mygdx.net.Packet25SendMessage;

public class PlayerMP extends Player{

	private InetAddress ipAddress;
	private int port;
	private Boolean localCheck;
	private GameClient client;
	private List<Float> previousXs, previousYs;
	private float messageTimer;
	
	private String message;
	
	private Boolean serverConnection;
	private int failedConnectionTimer;
	
	public PlayerMP(float x, float y, String name, InetAddress ipAddress, int port, Boolean localCheck, GameClient client) {

		super(x, y, name);
		// TODO Auto-generated constructor stub
		this.ipAddress = ipAddress;
		this.port = port;
		this.setLocalCheck(localCheck);
		this.client = client;
		previousXs = new CopyOnWriteArrayList<Float>();
		previousYs = new CopyOnWriteArrayList<Float>();
		message = "";
		messageTimer = 0;
		serverConnection = true;
		failedConnectionTimer = 0;
		
		
	}
	
	public PlayerMP(float x, float y, String name, InetAddress ipAddress, int port, Boolean localCheck) {

		super(x, y, name);
		// TODO Auto-generated constructor stub
		this.ipAddress = ipAddress;
		this.port = port;
		this.setLocalCheck(localCheck);	
		previousXs = new CopyOnWriteArrayList<Float>();
		previousYs = new CopyOnWriteArrayList<Float>();
		message = "";
		messageTimer = 0;
		serverConnection = true;
		failedConnectionTimer = 0;
	}
	
	/**
	 * Determines whether the canvas should write the enemey's name or health
	 * @return
	 */
	public Boolean showHp() {
		if(Gdx.input.isKeyPressed(Keys.TAB)) {
			return true;
		}
		else {
			return false;
		}		
	}
	
	
	/**
	 * This draws the name of the respective player over their head. If the local player holds shift, the health of the enemy players will be shown instead.
	 * @param c
	 * @param drawHp
	 * @param LocalPlayer
	 */
	public void drawPlayerName(Canvas c, Boolean drawHp, Boolean LocalPlayer) {
		if(!drawHp || LocalPlayer) {//Draw name if TAB is not held. Also, if the player is a local player, don't draw hp
			c.drawName(name, x, y);
		}
		else if(!LocalPlayer){//Draws HP above player if key is pressed
			c.drawEnemyHp(hp, maxHp, x, y);
		}
	}
	
	public void setFailedConnectionTimer(int i) {
		failedConnectionTimer = i;
	}
	
	public int getFailedConnectionTimer() {
		return failedConnectionTimer;
	}
	
	public Boolean getServerConnection() {
		return serverConnection;
	}
	
	public void setServerConnection(Boolean b) {
		serverConnection = b;
	}
	
	public void drawMessage(Canvas c) {
		c.drawMessage(message, x, y);
	}
	
	public void clearMessage() {
		if(messageTimer <= 0 && !message.equals("")) {
			message = "";
		}
		else if(messageTimer > 0) {
			messageTimer -= Gdx.graphics.getDeltaTime();
		}
	}
	
	public void setMessage(List<Packet> packetQueue, String message) {
		this.message = "\"" + message + "\"";
		if(this.message != null) {
			if(!this.message.equals("")) {
				Packet25SendMessage p = new Packet25SendMessage(name, this.message);
				packetQueue.add(p);
			}
		}
		messageTimer = 5;
		
	}
	
	public void setMessage(String message) {
		this.message = message;
		
		messageTimer = 5;
		
	}
		
	

	/**
	 * Updates the player's coordinates based on the current key being pressed.
	 */
	public void updateXY(List<Packet> packetQueue) {
		
		normSpeed = 0;
		float dt = Gdx.graphics.getDeltaTime();

		if (playerState == PlayerStates.DEFAULT) {
			
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				oldY = y;
				normSpeed = speed;
				y += normSpeed * dt;
				direction = UP;
				loops += 1 * dt;
				
				Date now = new Date();
				long timeSent = now.getTime();
				
				Packet17AntiCheatMove p = new Packet17AntiCheatMove(name, direction, timeSent);
				packetQueue.add(p);

			} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				oldY = y;
				normSpeed = speed;
				y -= normSpeed * dt;
				direction = DOWN;
				loops += 1 * dt;
				
				Date now = new Date();
				long timeSent = now.getTime();
				
				Packet17AntiCheatMove p = new Packet17AntiCheatMove(name, direction, timeSent);
				packetQueue.add(p);

				
			} else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				oldX = x;
				normSpeed = speed;
				x -= normSpeed * dt;
				direction = LEFT;
				loops += 1 * dt;
				
				Date now = new Date();
				long timeSent = now.getTime();
				
				Packet17AntiCheatMove p = new Packet17AntiCheatMove(name, direction, timeSent);
				packetQueue.add(p);

				
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				oldX = x;
				normSpeed = speed;
				x += normSpeed * dt;
				direction = RIGHT;
				loops += 1 * dt;
				
				Date now = new Date();
				long timeSent = now.getTime();
				
				Packet17AntiCheatMove p = new Packet17AntiCheatMove(name, direction, timeSent);
				packetQueue.add(p);

			}
			
			hitBox.makeBox();
			//hitBox.printBoxBounds();
			//System.out.println(name + " is at: " + x + "," + y);
		}
		
		
		//System.out.println(direction);
		//System.out.println(loops);
		
		
	}
	
	public void checkItemUseOnline(List<Packet> packetQueue) {
		if(Gdx.input.isKeyJustPressed(Keys.A) && playerState != PlayerStates.STUNNED) {
			System.out.println("Ding");
			
			Packet18AntiCheatUseItem p = new Packet18AntiCheatUseItem(name);
			packetQueue.add(p);
			
			ItemTypes type = Item.lookupItemType(currentItemId);
			
			switch (type) {
			default:
				break;
			case BOW:
				System.out.println("I have a bow");
				break;
			case BOMB:
				System.out.println("I have a bomb");
				break;
			case BOOMERANG:
				System.out.println("I have a boomerang");
				break;
			}
		}
	}
	
	public void checkStun() {
		if(stunTimer != 0) {
			if (stunTimer > 0) {
				stunTimer -= Gdx.graphics.getDeltaTime();
			}
			if (stunTimer <= 0) {
				playerState = PlayerStates.DEFAULT;
				stunTimer = 0f;
			}
		}
		
		
	}
	
	public void updateState(List<Packet> packetQueue) {
		
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			Packet19AntiCheatUseSword p = new Packet19AntiCheatUseSword(name);
			packetQueue.add(p);
		}
		else {
			Packet20AntiCheatNoSword p = new Packet20AntiCheatNoSword(name);
			packetQueue.add(p);
		}
		
	}
	
	public void updatePreviousCoordinateLists(float x, float y) {
		this.previousXs.add(x);
		this.previousYs.add(y);
		
		if(previousXs.size()>10) {
			previousXs.remove(0);
		}
		if(previousYs.size()>10) {
			previousYs.remove(0);
		}
	}
	
	public void deadReckon() {
		normSpeed = 0;
		
		int xStillCount = 0; //Used to count whether right, left or still is most likely
		int xRightCount = 0;
		int xLeftCount = 0;
		
		for(int i = 0; i<previousXs.size()-1;i++) { //Counting by comparing the size of x (larger x than last means moving right and vice versa)
			for(int j = i + 1; j<previousXs.size()-1; j++) {
				if(previousXs.get(i) == previousXs.get(j)) {
					xStillCount += 1;
				}
				else if(previousXs.get(i)>previousXs.get(j)) {
					xLeftCount += 1;
				}
				else if(previousXs.get(i)<previousXs.get(j)) {
					xRightCount += 1;
				}
			}
		}
		
		int yStillCount = 0; //Used to count whether moving up or down or not at all
		int yUpCount = 0;
		int yDownCount = 0;
		
		for(int i = 0; i<previousYs.size();i++) { // A larger y means moving upward and vice versa
			for(int j = i + 1; j<previousYs.size(); j++) {
				if(previousYs.get(i) == previousYs.get(j)) {
					yStillCount += 1;
				}
				else if(previousYs.get(i)>previousYs.get(j)) {
					yDownCount += 1;
				}
				else if(previousYs.get(i)<previousYs.get(j)) {
					yUpCount += 1;
				}
			}
		}
		
		//Based on what direction is most likely, move the player to smooth movement.
		
		if(xStillCount > xRightCount && xStillCount > xLeftCount) {//Keeps player still in x
			//System.out.println(name + " is standing still.");
		}
		else if(xRightCount > xStillCount && xRightCount > xLeftCount) { //Moves player right
			//System.out.println(name + " is moving right.");
			oldX = x;
			normSpeed = speed;
			x += normSpeed *  Gdx.graphics.getDeltaTime();
			direction = RIGHT;
			loops += 1 *  Gdx.graphics.getDeltaTime();
			
		}
		else if(xLeftCount > xStillCount && xLeftCount > xRightCount) { //Moves player left
			//System.out.println(name + " is moving left.");
			oldX = x;
			normSpeed = speed;
			x -= normSpeed * Gdx.graphics.getDeltaTime();
			direction = LEFT;
			loops += 1 * Gdx.graphics.getDeltaTime();
		}
		
		if (yStillCount > yUpCount && yStillCount >yDownCount){//Keeps player still in y
			
		}else if(yDownCount > yStillCount && yDownCount > yUpCount){//Moves player up
			oldY = y;
			normSpeed = speed;
			y -= normSpeed * Gdx.graphics.getDeltaTime();
			direction = DOWN;
			loops += 1 * Gdx.graphics.getDeltaTime();
		}
		else if(yUpCount > yStillCount && yUpCount > yDownCount) {//Moves player down
			oldY = y;
			normSpeed = speed;
			y += normSpeed * Gdx.graphics.getDeltaTime();
			direction = UP;
			loops += 1 * Gdx.graphics.getDeltaTime();
		}
		//System.out.println(xStillCount);
	}
	
	/**
	 * Returns true if the player is the local one controlled by the keyboard.
	 * @return
	 */
	public Boolean localCheck() {//Returns true if the player is local
		return getLocalCheck();
	}

	public void setState(int stateInt) {
		
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setIpAddress(InetAddress i) {
		ipAddress = i;
	}
	
	public void setPort(int p) {
		port = p;
	}

	public Boolean getLocalCheck() {
		return localCheck;
	}

	public void setLocalCheck(Boolean localCheck) {
		this.localCheck = localCheck;
	}

	public void setOldX(float x) {
		oldX = x;
		
	}
	
	public void setOldY(float y) {
		oldY = y;
	}

	
	

}
