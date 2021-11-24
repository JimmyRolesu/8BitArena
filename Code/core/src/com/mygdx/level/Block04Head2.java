package com.mygdx.level;

import com.mygdx.entities.Player;

public class Block04Head2 extends Block{

	public Block04Head2(float x, float y, int height, int width) {
		super(x, y, height, width, 4);
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
