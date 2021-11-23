package com.mygdx.entities;

public enum PlayerStates {
	INVALID(-1),
	DEFAULT(0),
	ATTACKING(1),
	STUNNED(2);
	
	private int id;
	
	private PlayerStates(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	
}
