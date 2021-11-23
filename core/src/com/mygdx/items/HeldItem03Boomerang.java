package com.mygdx.items;

import com.badlogic.gdx.Gdx;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerStates;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class HeldItem03Boomerang extends HeldItem{

	private int direction;
	private int speed;
	private int damage;
	private int distanceTraveled;
	private int maxDistance;
	private Boolean returnB;

	public HeldItem03Boomerang(float x, float y, int direction, String owner) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 3, owner);
		isCollectable = false;
		this.direction = direction;
		speed = 300;
		damage = 1;
		distanceTraveled = 0;
		maxDistance = 150;
		frameLoopTime = 0.01f;
		frameLoop = frameLoopTime;
		returnB = false;
	}
	
	public HeldItem03Boomerang(float x, float y, int direction, String uuid, String owner) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 3, uuid, owner);
		isCollectable = false;
		this.direction = direction;
		speed = 300;
		damage = 1;
		distanceTraveled = 0;
		maxDistance = 150;
		frameLoopTime = 0.01f;
		frameLoop = frameLoopTime;
		returnB = false;
	}

	@Override
	public void doAction(Level l) {
		
		frameLoop -= Gdx.graphics.getDeltaTime();
		if(frameLoop <= 0) {
			updateFrame();
			frameLoop = frameLoopTime;
		}	
		
		if(distanceTraveled <= maxDistance &&!returnB) {
			switch (direction) {
			case 0:
				// down
				y -= speed * Gdx.graphics.getDeltaTime();
				break;
			case 1:
				// up
				y += speed * Gdx.graphics.getDeltaTime();
				break;
			case 2:
				// right
				x += speed * Gdx.graphics.getDeltaTime();
				break;
			case 3:
				// left
				x -= speed * Gdx.graphics.getDeltaTime();
				break;
			}
			
			distanceTraveled += speed*Gdx.graphics.getDeltaTime();

			this.getHitBox().makeBox(); // Update hitbox

			if (x < 0 || x > MultiplayerGame.WIDTH || y < 0 || y > MultiplayerGame.HEIGHT) {
				l.removeHeldItem(this);
			}
		}
		
		if(distanceTraveled >= maxDistance) {
			returnB = true;
		}
		
		if(returnB) {
			switch (direction) {
			case 0:
				// down
				y += speed * Gdx.graphics.getDeltaTime();
				break;
			case 1:
				// up
				y -= speed * Gdx.graphics.getDeltaTime();
				break;
			case 2:
				// right
				x -= speed * Gdx.graphics.getDeltaTime();
				break;
			case 3:
				// left
				x += speed * Gdx.graphics.getDeltaTime();
				break;
			}
			
			distanceTraveled -= speed*Gdx.graphics.getDeltaTime();

			this.getHitBox().makeBox(); // Update hitbox

			if (x < 0 || x > MultiplayerGame.WIDTH || y < 0 || y > MultiplayerGame.HEIGHT) {
				l.removeHeldItem(this);
			}
		}
		
		if(returnB && distanceTraveled <= 0) {
			l.removeHeldItem(this);
		}
		

	}

	@Override
	public void updateFrame() {
		if(frame<7) {
			frame++;
		}
		else {
			frame = 0;
		}

	}

	@Override
	public void onContact(Player p, Level l) {
		if(!(p.getName().equals(owner))) {
			System.out.println("Hit: " + p.getName());
			l.removeHeldItem(this);
			//p.setHp(p.getHp()-damage);
			p.pushBack(l, direction, p.getSize()/2);
			p.stunPlayer(0.25f);
		}
		else {
					
		}
	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return frame;
	}
	
	public int getDirection() {
		return direction;
	}

}
