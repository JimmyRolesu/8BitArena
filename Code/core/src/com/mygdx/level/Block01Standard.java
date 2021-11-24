package com.mygdx.level;

import com.mygdx.entities.Player;

public class Block01Standard extends Block{

	public Block01Standard(float x, float y, int height, int width) {
		super(x, y, height, width, 1); //We know the id is 00 because it is a standard block
		this.solid = true;
	}

	@Override
	public void onContact(Player p) {
		p.stop();
		
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
