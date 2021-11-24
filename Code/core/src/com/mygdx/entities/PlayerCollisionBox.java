package com.mygdx.entities;

import com.mygdx.game.MultiplayerGame;

public class PlayerCollisionBox extends CollisionBox{
	
	private Player player;
	
	int boxOffsetH, boxOffsetW; //Small x,y offsets to make it easier to squeeze through blocks

	public PlayerCollisionBox(int height, int width, Player player) {
		super(height, width, player.getX(), player.getY());
		this.player = player;
		boxOffsetH = height/20;
		boxOffsetW = width/20;
		makeBox();		
	}
	
	@Override
	public void makeBox() {
		
		boxTop = player.getY() + height-boxOffsetH;
		boxBottom = player.getY() - height+boxOffsetH;
		boxRight = player.getX() + width-boxOffsetW;
		boxLeft = player.getX() - width+boxOffsetW;
		
	}


}
