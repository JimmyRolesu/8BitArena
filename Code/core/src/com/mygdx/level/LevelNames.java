package com.mygdx.level;

public enum LevelNames {

	LEVELONE("level1.lvl"), LEVELTWO("level2.lvl"), LEVELTHREE("level3.lvl"), LEVELFOUR("level4.lvl");

	private String levelName;
	
	LevelNames(String levelName) {
		this.levelName = levelName;
	}
	
	public String getLevelName() {
		return levelName;
		
	}
}
