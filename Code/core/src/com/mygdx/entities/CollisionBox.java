package com.mygdx.entities;

import com.mygdx.game.MultiplayerGame;

public abstract class CollisionBox {

	protected int height;
	protected int width;
	protected float x,y; //Box centre
	protected float boxTop, boxBottom, boxLeft, boxRight; //Boundaries for the collision box
	
	public CollisionBox(int height, int width, float x, float y) {
		this.height = height/2;
		this.width = width/2;
		this.x = x;
		this.y = y;
		
	}
	
	public void printBoxBounds() {
		System.out.println("Top: "+ boxTop + " Bottom: "+ boxBottom + "Left: " + boxLeft + "Right: " + boxRight);
	}
	
	public void updatePosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Boolean isColliding(CollisionBox collider) {
		if(this.boxTop > collider.getBoxBottom() && this.boxBottom < collider.getBoxTop() && this.boxLeft < collider.getBoxRight() && this.boxRight > collider.getBoxLeft()) {
			//System.out.println("Is Colliding!");
			return true;
		}else {
			//System.out.println("Isn't Colliding...");
			return false;
		}
		
	}
	

	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
		
	}
	
	public abstract void makeBox();


	public float getBoxTop() {
		return boxTop;
	}


	public void setBoxTop(float boxTop) {
		this.boxTop = boxTop;
	}


	public float getBoxBottom() {
		return boxBottom;
	}


	public void setBoxBottom(float boxBottom) {
		this.boxBottom = boxBottom;
	}


	public float getBoxLeft() {
		return boxLeft;
	}


	public void setBoxLeft(float boxLeft) {
		this.boxLeft = boxLeft;
	}


	public float getBoxRight() {
		return boxRight;
	}


	public void setBoxRight(float boxRight) {
		this.boxRight = boxRight;
	}


	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}


	public float getY() {
		return y;
	}


	public void setY(float y) {
		this.y = y;
	}

}



