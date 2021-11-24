package com.mygdx.level;

import com.mygdx.entities.Player;

public class Block06Stairs extends Block {

	public Block06Stairs(float x, float y, int height, int width) {
		super(x, y, height, width, 6);
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
