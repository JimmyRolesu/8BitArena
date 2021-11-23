package com.mygdx.entities;

import com.mygdx.items.HeldItem;
import com.mygdx.items.Item;

public class HeldItemCollisionBox extends CollisionBox{

	private HeldItem item;
	
	public HeldItemCollisionBox(int height, int width, HeldItem item) {
		super(height, width, item.getX(), item.getY());
		this.item = item;
		makeBox();
		
	}

	@Override
	public void makeBox() {
		boxTop = item.getY() + height;
		boxBottom = item.getY() - height;
		boxRight = item.getX() + width;
		boxLeft = item.getX() - width;
		
	}


}
