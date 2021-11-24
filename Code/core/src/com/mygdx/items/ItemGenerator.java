package com.mygdx.items;

import java.util.Random;
import java.util.UUID;

import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.Item.ItemTypes;

public class ItemGenerator {
	
	private ItemTypes lastType, currentType;
	private int numItems;

	public ItemGenerator() {
		lastType = ItemTypes.INVALID;
		currentType = ItemTypes.INVALID;
		numItems = ItemTypes.values().length - 1; //Gets the length of the Item Types enum so it scales w/ items added
	}
	
	/**
	 * Generates a random item object. Returns null if there is an error.
	 * @return
	 */
	public Item generateRandomItem() {
		Item item = null;
		
		Random rd = new Random(); 
		
		//Random in range is: min - random * (max-min)
		float randomX = MultiplayerGame.TILESIZE + rd.nextFloat() * (MultiplayerGame.WIDTH-MultiplayerGame.TILESIZE*2);
		float randomY = MultiplayerGame.TILESIZE + rd.nextFloat() * (MultiplayerGame.HEIGHT-MultiplayerGame.TILESIZE*4);
		
		int itemId = rd.nextInt(numItems) + 1;
		ItemTypes type = Item.lookupItemType(itemId);
		
		if(type.equals(lastType)) {
			if(type.getId()>numItems) {
				type = Item.lookupItemType(1);
			}
			else {
				type = Item.lookupItemType(itemId+1);
			}			
		}
		currentType = type;
		
		//System.out.println(type);
		switch(type) {
		default:
		case HEART:
			item = new Item01Heart(randomX,randomY);
			break;
		case HEARTCONTAINER:
			item = new Item02HeartContainer(randomX,randomY);
			break;
		case FAIRY:
			item = new Item03Fairy(randomX, randomY);
			break;
		case CLOCK:
			item = new Item04Clock(randomX, randomY);
			break;
		case BOW:
			item = new Item05Bow(randomX, randomY);
			break;
		case BOMB:
			item = new Item06Bomb(randomX, randomY);
			break;
		case BOOMERANG: 
			item = new Item07Boomerang(randomX, randomY);
			break;
		
		}
		return item;
		
	}
	
	public static void main(String[] args) {
		ItemGenerator i = new ItemGenerator();
		Item item = i.generateRandomItem();
		System.out.println(item);
		
	}
}
