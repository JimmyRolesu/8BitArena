package com.mygdx.items;

import com.badlogic.gdx.Gdx;
import com.mygdx.entities.Player;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.level.Level;

public class Item01Heart extends Item{
	
	public Item01Heart(float x, float y) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 01);
		frameLoopTime = 0.2f;
		frameLoop = frameLoopTime;
	}
	
	public Item01Heart(float x, float y, String uuid) {
		super(x, y, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, 01, uuid);
		frameLoopTime = 0.2f;
		frameLoop = frameLoopTime;
	}

	@Override
	public void updateFrame() {
		if(frame == 0) {
			frame = 1;
		}
		else {
				frame = 0;
		}
		
	}

	@Override
	public void onContact(Player p, Level l) {
		p.addHP(2);
		l.removeItem(this);
		
	}

	@Override
	public int returnFrame() {
		// TODO Auto-generated method stub
		return frame;
	}

	@Override
	public void doAction(Level l) {
		frameLoop -= Gdx.graphics.getDeltaTime();
		if(frameLoop <= 0) {
			updateFrame();
			frameLoop = frameLoopTime;
		}		
	}

}
