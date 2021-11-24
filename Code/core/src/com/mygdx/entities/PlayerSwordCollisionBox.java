package com.mygdx.entities;

import com.mygdx.game.MultiplayerGame;

public class PlayerSwordCollisionBox extends CollisionBox{
	
	private Player player;
	
	int boxOffsetH, boxOffsetW; //Small x,y offsets to make it easier to squeeze through blocks

	public PlayerSwordCollisionBox(int height, int width, Player player) {
		super(height, width, player.getX(), player.getY());
		this.player = player;
		makeBox();		
	}
	
	@Override
	public void makeBox() {
		int direction = player.getDirection();
		//Create a different sword hitbox depending on the player direction
		switch(direction) {
		default:
		case 0://Down
			boxTop = player.getY();
			boxBottom = player.getY() - height*2;
			boxRight = player.getX() + width/2;
			boxLeft = player.getX() - width/2;
			break;
		case 1://Up
			boxTop = player.getY() + height*2;
			boxBottom = player.getY();
			boxRight = player.getX() + width/2;
			boxLeft = player.getX() - width/2;
			break;
		case 2://Right
			boxTop = player.getY() + height/2;
			boxBottom = player.getY() - height/2;
			boxRight = player.getX() + width*2;
			boxLeft = player.getX();
			break;
		case 3://Left
			boxTop = player.getY() + height/2;
			boxBottom = player.getY() - height/2;
			boxRight = player.getX() ;
			boxLeft = player.getX() - width*2;
			break;
		}
		
		
		
	}


}
