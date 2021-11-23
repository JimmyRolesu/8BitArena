package com.mygdx.items;

import java.util.UUID;

import com.mygdx.entities.BlockCollisionBox;
import com.mygdx.entities.CollisionBox;
import com.mygdx.entities.ItemCollisionBox;
import com.mygdx.entities.Player;
import com.mygdx.level.Block.BlockTypes;
import com.mygdx.level.Level;
import com.mygdx.managers.Canvas;

public abstract class Item {

	public static enum ItemTypes {
		INVALID(-01, 0),
		HEART(1, 0),
		HEARTCONTAINER(2, 0),
		FAIRY(3, 0),
		CLOCK(4, 0),
		BOW(5, 0),
		BOMB(6, 0),
		BOOMERANG(7, 0);

		private int id;
		private float weight; //Indicates the chance of the item spawning

		private ItemTypes(int id, float weight) {
			this.id = id;
			this.weight = weight;
		}

		public int getId() {
			return id;
		}
		
		public float getWeight() {
			return weight;
		}
	}

	protected float x;
	protected float y;
	private int height, width;
	private int id;
	private UUID uuid;
	private CollisionBox hitBox;
	protected int frame;
	protected Boolean isCollectable;
	protected float frameLoop, frameLoopTime;

	public Item(float x, float y, int height, int width, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		hitBox = new ItemCollisionBox(height, width, this);
		frame = 0;
		isCollectable = true;
		uuid = UUID.randomUUID();
		frameLoopTime = 0.5f;
		frameLoop = frameLoopTime;
		
	}
	
	public Item(float x, float y, int height, int width, int id, String uuid) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		hitBox = new ItemCollisionBox(height, width, this);
		frame = 0;
		isCollectable = true;
		this.uuid = UUID.fromString(uuid);
		frameLoopTime = 0.5f;
		frameLoop = frameLoopTime;
		
	}

	public static ItemTypes lookupItemType(int id) {
		for (ItemTypes i : ItemTypes.values()) {// Loops through all enums in Packets Types eg. login & disconnect
			if (i.getId() == id) { // Returns the packet if it matches the id.
				return i;
			}
		}
		return ItemTypes.INVALID; // Otherwise returns an invalid packet
	}

	public void drawItem(Canvas c) {
		ItemTypes type = lookupItemType(this.id);
		c.drawItem(type, this);
		// hitBox.printBoxBounds();

	}
	
	public abstract void doAction(Level l);

	public abstract void updateFrame();

	public void onContact(Player p, Level level) {
		level.removeItem(this);
	}

	public abstract int returnFrame();

	public CollisionBox getHitBox() {
		return hitBox;
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public String getUUIDasString() {
		return uuid.toString();
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

}
