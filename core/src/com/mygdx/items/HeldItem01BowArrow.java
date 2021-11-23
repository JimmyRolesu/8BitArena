package com.mygdx.items;

import com.badlogic.gdx.Gdx;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class HeldItem01BowArrow extends HeldItem {

	private int direction;
	private int speed;
	private int damage;

	public HeldItem01BowArrow(float x, float y, int direction, String owner) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 1, owner);
		isCollectable = false;
		this.direction = direction;
		speed = 600;
		damage = 2;
	}
	
	public HeldItem01BowArrow(float x, float y, int direction, String uuid, String owner) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 1, uuid, owner);
		isCollectable = false;
		this.direction = direction;
		speed = 600;
		damage = 2;
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
			l.removeHeldItem(this);
		}

	}

	@Override
	public void updateFrame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onContact(Player p, Level l) {
		if(!(p.getName().equals(owner))) {
			System.out.println("Hit: " + p.getName());
			l.removeHeldItem(this);
			p.setHp(p.getHp()-damage);
			p.pushBack(l, direction, p.getSize());
		}
		else {
					
		}
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
