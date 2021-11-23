package com.mygdx.level;

import com.mygdx.entities.Player;

public class Block03Head1 extends Block{

	public Block03Head1(float x, float y, int height, int width) {
		super(x, y, height, width, 3);
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
