package com.mygdx.items;

import com.badlogic.gdx.Gdx;
import com.mygdx.entities.Player;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class Item08Arrow extends Item {
	
	private int direction;
	private int speed;

	public Item08Arrow(float x, float y, int direction) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 8);
		isCollectable = false;
		this.direction = direction;
		speed = 600;
	}
	
	public Item08Arrow(float x, float y, int direction, String uuid) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 8, uuid);
		isCollectable = false;
		this.direction = direction;
		speed = 600;
	}

	@Override
	public void doAction(Level l) {
		switch(direction) {
		case 0:
			//down
			y-=speed*Gdx.graphics.getDeltaTime();
			break;
		case 1:
			//up
			y+=speed*Gdx.graphics.getDeltaTime();
			break;
		case 2:
			//right
			x+=speed*Gdx.graphics.getDeltaTime();
			break;
		case 3:
			//left
			x-=speed*Gdx.graphics.getDeltaTime();
			break;
		}
		
		this.getHitBox().makeBox(); //Update hitbox
		
		if(x < 0 || x > MultiplayerGame.WIDTH || y < 0 || y > MultiplayerGame.HEIGHT) {
			l.removeItem(this);
		}

	}

	@Override
	public void updateFrame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onContact(Player p, Level l) {
		// TODO Auto-generated method stub

	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getDirection() {
		return direction;
	}

}
