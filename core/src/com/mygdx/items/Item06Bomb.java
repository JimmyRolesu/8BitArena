package com.mygdx.items;

import com.mygdx.entities.Player;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class Item06Bomb extends Item {

	public Item06Bomb(float x, float y) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 06);
		// TODO Auto-generated constructor stub
	}
	
	public Item06Bomb(float x, float y, String uuid) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 06, uuid);
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
