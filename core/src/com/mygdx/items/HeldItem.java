package com.mygdx.items;

import java.util.UUID;

import com.mygdx.entities.CollisionBox;
import com.mygdx.entities.HeldItemCollisionBox;
import com.mygdx.entities.ItemCollisionBox;
import com.mygdx.entities.Player;
import com.mygdx.items.Item.ItemTypes;
import com.mygdx.level.Level;
import com.mygdx.managers.Canvas;

public abstract class HeldItem {

	public static enum HeldItemTypes {
		INVALID(-01, 0),
		BOWARROW(1, 0),
		BOMB(2, 0),
		BOOMERANG(3, 0);	

		private int id;
		private float weight; //Indicates the chance of the item spawning

		private HeldItemTypes(int id, float weight) {
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
	protected int height;
	protected int width;
	protected int id;
	protected UUID uuid;
	protected CollisionBox hitBox;
	protected int frame;
	protected Boolean isCollectable;
	protected String owner;
	protected float frameLoopTime, frameLoop;

	public HeldItem(float x, float y, int height, int width, int id, String owner) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		hitBox = new HeldItemCollisionBox(height, width, this);
		frame = 0;
		isCollectable = true;
		uuid = UUID.randomUUID();
		this.owner = owner;
		frameLoopTime = 0.5f;
		frameLoop = frameLoopTime;
	}
	
	public HeldItem(float x, float y, int height, int width, int id, String uuid, String owner) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		hitBox = new HeldItemCollisionBox(height, width, this);
		frame = 0;
		isCollectable = true;
		this.uuid = UUID.fromString(uuid);
		this.owner = owner;
		frameLoopTime = 0.5f;
		frameLoop = frameLoopTime;
	}

	public static HeldItemTypes lookupHeldItemType(int id) {
		for (HeldItemTypes i : HeldItemTypes.values()) {// Loops through all enums in Packets Types eg. login & disconnect
			if (i.getId() == id) { // Returns the packet if it matches the id.
				return i;
			}
		}
		return HeldItemTypes.INVALID; // Otherwise returns an invalid packet
	}

	public void drawHeldItem(Canvas c) {
		HeldItemTypes type = lookupHeldItemType(this.id);
		c.drawHeldItem(type, this);
		// hitBox.printBoxBounds();

	}
	
	public abstract void doAction(Level l);

	public abstract void updateFrame();

	public abstract void onContact(Player p, Level level);
	

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
	
	public String getOwner() {
		return owner;
	}

}
