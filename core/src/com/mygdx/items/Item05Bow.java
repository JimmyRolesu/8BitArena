package com.mygdx.items;

import com.mygdx.entities.Player;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class Item05Bow extends Item {
	
	public Item05Bow(float x, float y) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 05);
		// TODO Auto-generated constructor stub
	}
	
	public Item05Bow(float x, float y, String uuid) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 05, uuid);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateFrame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onContact(Player p, Level l) {
		p.setCurrentItem(getId());
		l.removeItem(this);
		
	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doAction(Level l) {
		// TODO Auto-generated method stub
		
	}

}
