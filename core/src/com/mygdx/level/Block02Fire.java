package com.mygdx.level;

import com.mygdx.entities.Player;

public class Block02Fire extends Block{
	
	private Level level;

	public Block02Fire(float x, float y, int height, int width, Level level) {
		super(x, y, height, width, 2);
		this.level = level;
		this.solid = true;
	}

	@Override
	public void onContact(Player p) {
		p.setHp(p.getHp()-1);
		p.pushBack(level, p.getSize());
		
	}
	
	public void updateFrame() {
		if(frame == 0) {
			frame = 1;
		}
		else {
				frame = 0;
		}		
	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return frame;
	}
}
