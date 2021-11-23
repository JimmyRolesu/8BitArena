package com.mygdx.entities;

import com.mygdx.level.Block;

public class BlockCollisionBox extends CollisionBox{
	
	private Block block;
	
	public BlockCollisionBox(int height, int width, Block block) {
		super(height, width, block.getX(), block.getY());
		this.block = block;
		makeBox();
		
	}

	@Override
	public void makeBox() {
		boxTop = block.getY() + height;
		boxBottom = block.getY() - height;
		boxRight = block.getX() + width;
		boxLeft = block.getX() - width;
		
	}


}
