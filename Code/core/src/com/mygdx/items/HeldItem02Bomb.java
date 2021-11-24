package com.mygdx.items;

import com.badlogic.gdx.Gdx;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class HeldItem02Bomb extends HeldItem{
	
	private int damage;
	private float countdown, explosionTimer, explosionTime;
	private Boolean exploded;
	private int explodedHeight, explodedWidth;

	public HeldItem02Bomb(float x, float y, String owner) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 2, owner);
		damage = 6;
		countdown = 3f;
		explosionTime = 0.75f;
		explosionTimer = explosionTime;
		exploded = false;
		explodedHeight = height*3;
		explodedWidth = width*3;
	}
	
	public HeldItem02Bomb(float x, float y, String uuid, String owner) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 2, uuid, owner);
		damage = 6;
		countdown = 3f;
		explosionTimer = 0.5f;
		exploded = false;
	}

	@Override
	public void doAction(Level l) {
		if(countdown <=0) {
			exploded = true;
		}
		if(!exploded) {
			countdown -= Gdx.graphics.getDeltaTime();
		}
		else if(exploded) {
			explosionTimer -= Gdx.graphics.getDeltaTime();
			height = explodedHeight;
			width = explodedWidth;
			hitBox.makeBox();
		}
		
		if(explosionTimer <= 0) {
			l.removeHeldItem(this);
		}
		
	}
	
	@Override
	public void onContact(Player p, Level l) {
		if(!p.getName().equals(owner) && exploded) {
			System.out.println(p.getName()+" hit!");
			p.pushBack(l, p.getSize()*3);
			p.setHp(p.getHp()-damage);
		}
		

	}

	@Override
	public void updateFrame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Boolean isExploded() {
		return exploded;
	}
	
	public float getExplosionTimer() {
		return explosionTimer;
	}
	
	public float getExplosionTime() {
		return explosionTime;
	}

}
