package com.mygdx.level;

import java.util.Random;

public class LevelRandomiser {
	private LevelNames[] levels = LevelNames.values();
	private LevelNames chosenLevel;
	
	public LevelRandomiser() {
		
	}
	
	public String generateLevelName() {
		Random rd = new Random();
		chosenLevel = levels[rd.nextInt(levels.length)];
		return chosenLevel.getLevelName();
	}
}
