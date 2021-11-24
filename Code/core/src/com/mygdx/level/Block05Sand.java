package com.mygdx.level;

import com.mygdx.entities.Player;

public class Block05Sand extends Block {

	public Block05Sand(float x, float y, int height, int width) {
		super(x, y, height, width, 5);
	}

	@Override
	public void onContact(Player p) {
		
		
	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateFrame() {
		// TODO Auto-generated method stub
		
	}

}
