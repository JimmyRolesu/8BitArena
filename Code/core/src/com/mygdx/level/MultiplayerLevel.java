package com.mygdx.level;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.entities.PlayerStates;
import com.mygdx.items.Item;
import com.mygdx.items.ItemGenerator;
import com.mygdx.net.GameClient;
import com.mygdx.net.GameServer;
import com.mygdx.net.Packet00Login;

public class MultiplayerLevel extends Level{
	
	//private List<PlayerMP> playerList;
	
	private PlayerMP localP;

	public MultiplayerLevel(String map) {
		super(map);
	}
	
	public void addLocalPlayer(String pName) {
		playerList.add(localP);
	}

	@Override
	public void addPlayer(Player p) {
		playerList.add((PlayerMP) p); //Cast to mp player
		
	}
	
	@Override
	public void removePlayer(Player p) {
		playerList.remove((PlayerMP)p);
		
	}

	public List<Player> returnPlayerList(){
		return playerList;
	}
	
	public void checkPlayerCollisions(List<PlayerMP> list) {
		for(PlayerMP p : list) {
			if(p.localCheck()) {
				for (PlayerMP p2 : list) {
					if (!(p.getName().equals(p2.getName()))) {
						if (p.getHitbox().isColliding(p2.getHitbox())) {
							p.stop();
						}
					}
				}
			}		
		}
	}
	
	public void checkPlayerCollisionsServer(List<PlayerMP> list) {
		for (PlayerMP p : list) {
			for (PlayerMP p2 : list) {
				if (!(p.getName().equals(p2.getName()))) {
					if (p.getHitbox().isColliding(p2.getHitbox())) {
						p.stop();
					}
				}
			}

		}
	}
	
	public void checkPlayerSwordCollisions(List<PlayerMP> list) {
		for (PlayerMP p : list) {
			for (PlayerMP p2 : list) {
				if (!(p.getName().equals(p2.getName()))) {
					if (p.getHitbox().isColliding(p2.getSwordHitbox()) && p2.getState().equals(PlayerStates.ATTACKING)) {
						p.setHp(p.getHp() - 2);
						p.pushBack(this, p2.getDirection(), p2.getSize());
					}
				}
			}
		}
	}
	
	
	
	public void checkItemCollision(List<PlayerMP> playerList, GameServer server) {
		for(PlayerMP p : playerList) {
			for (Item i : getItemList()) {
				if (p.getHitbox().isColliding(i.getHitBox())) {
					i.onContact(p, this);				
					getItemList().remove(i);
					
					server.removeGameItem(i); //No longer in use
					
				}
			}
		}	
	}
	
	public void checkItemCollision(PlayerMP p) {
		for (Item i : getItemList()) {
			if (p.getHitbox().isColliding(i.getHitBox())) {
				i.onContact(p, this);
				//System.out.println("Touching "+ i);
			}
		}
	}
	
	public void randomItemTimer(GameServer server) {
		timer += Gdx.graphics.getDeltaTime();
		
		if(getItemList().size()>maxNumItems) { //Timer cannot increase while there are max items spawned
			timer = 0;
		}
		if(timer >= 2f) {
			if(getItemList().size()<maxNumItems) {
				addRandomItem(server);
			}
			timer = 0;
		}
	}

	private void addRandomItem(GameServer server) {
		ItemGenerator i = new ItemGenerator();
		Item temp = i.generateRandomItem(); //Created the random item
		System.out.println(Item.lookupItemType(temp.getId()));
		
		if(!temp.equals(null)) { //Make sure it is not null
			Boolean canAdd = true;
			for(Block b : getBlockList()) { //Goes through the block map can checks whether or not an item spawns on a block. 
				if(temp.getHitBox().isColliding(b.getHitBox())) {
					System.out.println("P");
					canAdd = false; //If colliding with any blocks on the map, don't spawn.
					
				}
			}
			if(canAdd) {
				getItemList().add(temp); //Add to level list
				this.itemsToAdd.add(temp);
				//server.addGameItem(temp); //No longer in used.
				
			}
			
		}		
	}

}
