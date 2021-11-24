package com.mygdx.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.HeldItem01BowArrow;
import com.mygdx.items.HeldItem02Bomb;
import com.mygdx.items.Item;
import com.mygdx.items.Item.ItemTypes;
import com.mygdx.items.Item01Heart;
import com.mygdx.items.Item05Bow;
import com.mygdx.items.Item08Arrow;
import com.mygdx.level.Block;
import com.mygdx.level.Level;
import com.mygdx.managers.Canvas;
import com.mygdx.net.GameClient;
import com.mygdx.net.Packet02Movement;
import com.mygdx.net.Packet06SendState;

public class Player {
	
	//These determine what direction the player is facing. They are constant and cannot be changed. Public so other classes can access them.
	public static final int DOWN = 0;
	public static final int UP = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;
	
	protected static final int FINALHP = 16;//The absolute final amount of HP a player can have. Max Hp cannot surpass this.
	
	
	
	protected float x,y, oldX, oldY; //Current position of the player as well as the last position.
	protected int maxHp, hp, stepCount, strength;
	protected float speed, normSpeed; //Speed in which player moves.
	//protected Texture img;
	//protected Texture sheet;
	protected int direction; //Current direction of the player.
	protected float loops; //Increments each step;
	protected String name; //The name of the player.
	protected PlayerStates playerState;
	protected int playerSize; //How big the player is and how big their hitbox will be.
	protected CollisionBox hitBox;
	protected CollisionBox swordHitBox;
	protected Boolean stunned;
	protected Boolean isDead;
	protected int currentItemId;//
	protected int roundsWon;
	protected float stunTimer;
	
	/**
	 * Creates a player. This can be used for local games as it does not have any online functionality.
	 * @param x
	 * @param y
	 * @param name
	 */
	public Player(float x, float y, String name) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.name = name;
		strength = 2;// How strong player attacks are.
		maxHp = 12;
		hp = maxHp;
		normSpeed = 0;
		speed = 100;
		loops = 0;
		direction = RIGHT;
		stepCount = 0;
		playerState = PlayerStates.DEFAULT;
		playerSize = MultiplayerGame.TILESIZE;
		hitBox = new PlayerCollisionBox(playerSize, playerSize, this);
		swordHitBox = new PlayerSwordCollisionBox(playerSize, playerSize, this);
		stunned = false;
		isDead = false;
		currentItemId = 0;
		roundsWon = 0;
		stunTimer = 0;
	}
	
	/**
	 * This is used to reset any player values that need to be reset at the beginning of a new round. Some values such as "roundsWon" stay persistent even after reset.
	 */
	public void reset() {
		strength = 2;
		maxHp = 12;
		hp = maxHp;
		normSpeed = 0;
		speed = 100;
		loops = 0;
		direction = RIGHT;
		stepCount = 0;
		playerState = PlayerStates.DEFAULT;
		playerSize = MultiplayerGame.TILESIZE;
		hitBox = new PlayerCollisionBox(playerSize, playerSize, this);
		swordHitBox = new PlayerSwordCollisionBox(playerSize, playerSize, this);
		stunned = false;
		isDead = false;
		currentItemId = 0;
	}

	/**
	 * This prevents the player from moving by setting their current coordinates to their last one. Can be used for collision.
	 */
	public void stop() {
		x = oldX;
		y = oldY;
	}

	/**
	 * This returns a player state based on the id value of the state. It will look through all the different values id and compare until it is found.
	 * @param id
	 * @return
	 */
	public static PlayerStates lookupState(int id) {
		for (PlayerStates s : PlayerStates.values()) {// Loops through all enums in Player States
			if (s.getId() == id) { // Returns the state if it matches the id.
				return s;
			}
		}
		return PlayerStates.INVALID; // Otherwise returns an invalid state
	}

	/**
	 * When the player collides with a hitbox with no specific direction. It will push the player back as long as they won't hit any parts of the level.
	 * @param l
	 * @param force - Force in the form of the amount of distance the player is pushed back.
	 */
	public void pushBack(Level l, int force) {
		
		int push = force;
		switch(direction) { //Where should the player be pushed back?
		default:
		case 0: //Down
			y += push;
			break;
		case 1: //Up
			y -= push;
			break;
		case 2: //Right
			x -= push;
			break;
		case 3: //Left
			x += push;
			break;			
		}		
		hitBox.makeBox(); //Update the players hitbox
		
		for(Block b : l.getBlockList()) {
			if(this.getHitbox().isColliding(b.getHitBox()) && b.isSolid()) { //Can the player be pushed back here? Check the level for blocks that might be in the way.
				x = oldX;
				y = oldY;
				//hitBox.makeBox();
			}
			else {
				oldX = x;
				oldY = y;
			}
			//System.out.println("Can't spawn here");
		}					
	}

	/**
	 * A version of pushback that takes an enemy player direction and pushes accordingly. Like the normal pushback, it checks the level to see if the player isn't hitting any blocks before pushing back.
	 * @param l
	 * @param direction
	 */
	public void pushBack(Level l, int direction, int force) {
	
		int push = force;
		switch (direction) {
		default:
		case 0: // Down
			y -= push;
			break;
		case 1: // Up
			y += push;
			break;
		case 2: // Right
			x += push;
			break;
		case 3: // Left
			x -= push;
			break;
		}
	
		hitBox.makeBox(); //Update the player's hitbox
		
		for(Block b : l.getBlockList()) {
			if(this.getHitbox().isColliding(b.getHitBox()) && b.isSolid()) {
				x = oldX;
				y = oldY;
			}
			//System.out.println("Can't spawn here");
		}
	
		oldX = x;
		oldY = y;
	
	}

	/**
	 * Updates the player's coordinates based on the current key being pressed. It also changes the current direction the player is facing and updates their "loop" counter, used to determine what step they are taking for animation.
	 */
	public void updateXY() {
		
		normSpeed = 0;
		float dt = Gdx.graphics.getDeltaTime();

		if (playerState == PlayerStates.DEFAULT) {
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				oldY = y;
				normSpeed = speed;
				y += normSpeed * dt;
				direction = UP;
				loops += 1 * dt;

			} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				oldY = y;
				normSpeed = speed;
				y -= normSpeed * dt;
				direction = DOWN;
				loops += 1 * dt;
				
			} else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				oldX = x;
				normSpeed = speed;
				x -= normSpeed * dt;
				direction = LEFT;
				loops += 1 * dt;
				
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				oldX = x;
				normSpeed = speed;
				x += normSpeed * dt;
				direction = RIGHT;
				loops += 1 * dt;
			}
			
			hitBox.makeBox();
			//hitBox.printBoxBounds();
		}
		
		
		//System.out.println(direction);
		//System.out.println(loops);
		
		//System.out.println(name + " is at: " + x + "," + y);
		
	}
	
	/**
	 * This checks what item they player is currently holding (if any) and will perform the relevant action when the action key is pressed.
	 * @param l
	 */
	public void checkItemUse(Level l) {
		
		ItemTypes type = Item.lookupItemType(currentItemId);
		
		if(Gdx.input.isKeyJustPressed(Keys.A) && playerState != PlayerStates.STUNNED) {
			//System.out.println("A");
			switch (type) {
			default:
				break;
			case BOW:
				System.out.println("I have a bow");
				l.addHeldItem(new HeldItem01BowArrow(x, y, direction, name));
				break;
			case BOMB:
				System.out.println("I have a bomb");
				l.addHeldItem(new HeldItem02Bomb(x,y,name));
				break;
			case BOOMERANG:
				System.out.println("I have a boomerang");
				break;
			}
		}

	}
	
	/**
	 * Updates player state based on what is going on. Eg. pressing SPACE makes the player attack.
	 */
	public void updateState() {
		if(stunTimer >= 0) {
			stunTimer -= Gdx.graphics.getDeltaTime();
		}
		if(stunTimer <= 0) {
			playerState = PlayerStates.DEFAULT;
			stunTimer = 0f;
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE) && playerState != PlayerStates.STUNNED) {
			playerState = PlayerStates.ATTACKING;
			swordHitBox.makeBox();
			stepCount = 1;
		}
		else {
			playerState = PlayerStates.DEFAULT;
		}
		
		System.out.println(name+"'s "+ "state is: " + playerState);
	}
	
	/**
	 * Timer that loops walk animation for player
	 */
	public void loopTimer(Canvas canvas) {
		if (loops > 0.25f) {
			this.updatePcounter();
			loops = 0;
		}
	}
	
	private void updatePcounter() {
		if (stepCount == 0) {
			stepCount = 1;
		}
		else if (stepCount == 1) {
			stepCount = 0;
		}
	}
	
	/**
	 * Draws player to the screen.
	 * @param c
	 */
	public void drawPlayer(Canvas c) {
		c.drawPlayer(x, y, direction, stepCount, playerState);
	}
	
	public void areaEdgeCheck() {
		if (x > MultiplayerGame.WIDTH - playerSize/2) {
			x = oldX;
		}
		else if (x < playerSize/2) {
			x = oldX;
		}
		else if (y < playerSize/2 ) {
			y = oldY;
		}
		else if (y > MultiplayerGame.HUDSPACE + playerSize/2) {
			y = oldY;
		}
		
		
	}
	
	/**
	 * Updates the "loops" counter that is used to determine what sprite for the player is drawn. Will create the illusion of movement.
	 * @param dt
	 */
	public void updateWalkLoop(){
		float dt = Gdx.graphics.getDeltaTime();
		loops += 1*dt;
	}

	public int getDirection() {
		return direction;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	
	public float getOldX() {
		return oldX;
	}
	
	public float getOldY() {
		return oldY;
	}
	
	public int getHp() {
		return hp;
	}
	
	public void setHp(int hp) {
		if(this.hp != 0) {
			this.hp = hp;
		}
		if(this.hp < 0) {
			this.hp = 0;
		}
	}
	
	public void setX(float x) {
		oldX = this.x;
		this.x = x;
		
	}

	public void setY(float y) {
		oldY = this.y;
		this.y = y;
		
	}

	public void setDirection(int direction) {
		this.direction = direction;
		
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public int getMaxHp() {
		return maxHp;
	}
	
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}
	
	public String getName() {
		return name;
	}
	
	public void stunPlayer(float t) {
		playerState = PlayerStates.STUNNED;
		stunTimer = t;
	}
	
	
	/**
	 * Returns the player state value so it can be used in sprite sheets
	 * @return
	 */
	public PlayerStates getState() {
		return playerState;
	}
	
	public void setState(PlayerStates s) {
		playerState = s;
		if(s.equals(PlayerStates.ATTACKING)) {
			swordHitBox.makeBox();
		}
	}
	
	public CollisionBox getHitbox() {
		return hitBox;
	}
	
	public CollisionBox getSwordHitbox() {
		return swordHitBox;
	}
	
	public int getSize() {
		return playerSize;
	}
	
	/**
	 * The amount of damage a player will do when attacking with the sword.
	 * @return
	 */
	public int getDamage() {
		return strength;
	}
	
	/**
	 * Increases the player health by the amount specified. If the player gets more than their max health, it will just stay the same.
	 * @param extraHp
	 */
	public void addHP(int extraHp) {
		hp +=extraHp;
		if (hp >=maxHp){
			hp = maxHp;
		}
		
	}

	/**
	 * Some items can increase the maximum health. This increases the max hp as specified. Max hp cannot be exceeded by a certain amount, shown with "FINALHP".
	 * @param extraMaxHp
	 */
	public void increaseMaxHp(int extraMaxHp) {
		maxHp += extraMaxHp;	
		if (maxHp >= FINALHP) {
			maxHp = FINALHP;
		}
		
		addHP(2);
		
	}
	
	public int getCurrentItemId() {
		return currentItemId;
		
	}
	
	/**
	 * Sets the player item based on the id of that item.
	 * @param itemId
	 */
	public void setCurrentItem(int itemId) {
		currentItemId = itemId;
		//System.out.println("Set to:" + itemId);
	}
	
	public void setIsdead(Boolean b) {
		isDead = b;
	}
	
	public Boolean getIsDead() {
		return isDead;
	}
	
	/**
	 * Increase rounds won by 1.
	 */
	public void roundWon() {
		roundsWon++;
	}
	
	public int getRoundsWon() {
		return roundsWon;
	}

	
}
