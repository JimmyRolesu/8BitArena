package com.mygdx.level;

import com.mygdx.entities.BlockCollisionBox;
import com.mygdx.entities.CollisionBox;
import com.mygdx.entities.Player;
import com.mygdx.managers.Canvas;

public abstract class Block {
	
	public static enum BlockTypes {
		INVALID(-01), STANDARD(1), FIRE(2), HEAD1(3), HEAD2(4), SAND(5), STAIRS(6), BLACK(7), PURPLE(8);
		
		private int id;
		
		private BlockTypes(int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
	}
	
	private float x,y;
	private int height, width;
	private int id;
	private CollisionBox hitBox;
	protected int frame;
	protected Boolean solid;
	
	
	public Block(float x, float y, int height, int width, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		this.solid = false;
		
		hitBox = new BlockCollisionBox(height, width, this);
		frame = 0;
	}
	
	public static BlockTypes lookupBlockType(int id) {
		for (BlockTypes b : BlockTypes.values()) {//Loops through all enums in Packets Types eg. login & disconnect
			if(b.getId() == id) { //Returns the packet if it matches the id.
				return b;
			}
		}
		return BlockTypes.INVALID; //Otherwise returns an invalid packet
	}
	
	public void drawBlock(Canvas c) {
		BlockTypes type = lookupBlockType(this.id);
		c.drawBlock(type, this);
		//hitBox.printBoxBounds();
		
	}
	public abstract void updateFrame();
	
	public abstract void onContact(Player p);
	
	public abstract int returnFrame();
	
	public CollisionBox getHitBox() {
		return hitBox;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getId() {
		return id;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public Boolean isSolid() {
		return solid;
	}
	
}

