package com.mygdx.entities;

import com.mygdx.items.Item;

public class ItemCollisionBox extends CollisionBox{
	
	private Item item;
	
	public ItemCollisionBox(int height, int width, Item item) {
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
