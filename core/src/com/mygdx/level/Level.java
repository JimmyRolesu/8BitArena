package com.mygdx.level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.entities.CollisionBox;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.HeldItem;
import com.mygdx.items.Item;
import com.mygdx.items.Item01Heart;
import com.mygdx.items.Item02HeartContainer;
import com.mygdx.items.ItemGenerator;
import com.mygdx.level.Block.BlockTypes;
import com.mygdx.managers.Canvas;
import com.mygdx.net.GameServer;

public abstract class Level {
	
	protected List<Player> playerList;
	private List<Block> blockList;
	protected List<Item> itemList;
	protected List<HeldItem> heldItemList;
	protected List<Item> itemsToRemove, itemsToAdd;
	protected List<HeldItem> heldItemsToAdd;
	private List<CollisionBox> collisionMap;
	protected int[][] levelArray;
	protected String mapPath;
	protected float blockFrameLoop;
	protected Item test, test2;
	protected int maxNumItems;
	
	protected float timer;
	
	public Level(String map) {
		this.mapPath = "levels/"+map;
		setBlockList(new CopyOnWriteArrayList<Block>());
		setCollisionMap(new CopyOnWriteArrayList<CollisionBox>());
		
		itemList = new CopyOnWriteArrayList<Item>();
		heldItemList = new CopyOnWriteArrayList<HeldItem>();
		itemsToRemove = new CopyOnWriteArrayList<Item>();
		itemsToAdd = new CopyOnWriteArrayList<Item>();
		heldItemsToAdd = new CopyOnWriteArrayList<HeldItem>();
		playerList = new CopyOnWriteArrayList<Player>();
		
		blockFrameLoop = 0f;
		timer = 0;
		maxNumItems = 5;
		
		fileToArray(mapPath);
		buildLevel();
		
		
		
	}
	
	public abstract void addPlayer(Player p);
	
	public abstract void removePlayer(Player p);

	private void fileToArray(String map){

        String text = "";
        String[] lines = new String[MultiplayerGame.YSPLIT];
        int count = 0;
        int[][] split2d = new int[MultiplayerGame.XSPLIT][MultiplayerGame.YSPLIT];
        String[] split = new String[MultiplayerGame.XSPLIT];

        try{
            FileHandle f = Gdx.files.internal(map);
  
        	InputStream is = f.read(); //File reader opens the current map and puts it into buffered reader
            BufferedReader bfr = new BufferedReader(new InputStreamReader(is));

            if(is != null){
                while((text = bfr.readLine()) != null){ //Puts each line of reader into a array
                    lines[count] = text;
                    count++;
                }
                is.close();
            }

            for(int y = 0; y<MultiplayerGame.YSPLIT; y++){ //Splits that array into a 2D array containing map information
                split = lines[y].split(",");
                for(int x = 0; x<MultiplayerGame.XSPLIT; x++){
                    split2d[x][y] = Integer.parseInt(split[x]);
                }
            }

            bfr.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
        System.out.println(split2d[MultiplayerGame.XSPLIT-1][MultiplayerGame.YSPLIT-1]);
        
        levelArray = split2d;
        
        //return split2d;
    }
	
	private void buildLevel() {
		 int xOffset = MultiplayerGame.TILESIZE/2;
	        int yOffset = MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE/2;
	        for(int y = 0; y<MultiplayerGame.YSPLIT; y++){
	            for(int x = 0; x<MultiplayerGame.XSPLIT; x++){
	                if(levelArray[x][y] == 1){
	                    getBlockList().add(new Block01Standard(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
	                }
	                else if(levelArray[x][y] == 2){
	                    getBlockList().add(new Block02Fire(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, this));
	                }
	                else if(levelArray[x][y] == 3) {
	                	getBlockList().add(new Block03Head1(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
					} 
	                else if (levelArray[x][y] == 4) {
	                	getBlockList().add(new Block04Head2(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
					} 
	                else if (levelArray[x][y] == 5) {
	                	getBlockList().add(new Block05Sand(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
					} 
	                else if (levelArray[x][y] == 6) {
	                	getBlockList().add(new Block06Stairs(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
					}
	                else if (levelArray[x][y] == 7) {
	                	getBlockList().add(new Block07Black(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
					}
	                else if (levelArray[x][y] == 8) {
	                	getBlockList().add(new Block08Purple(xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE));
					}
	                
	                
	                
	                xOffset += MultiplayerGame.TILESIZE;
	            }
	            xOffset = MultiplayerGame.TILESIZE/2;
	            yOffset -= MultiplayerGame.TILESIZE;
	        }

	}
	
	public void drawLevel(Canvas c) {
		for(Block b : getBlockList()) {
			b.drawBlock(c);
			
		}
		for(Item i: getItemList()) {
			i.drawItem(c);
		}
		for(HeldItem i: heldItemList) {
			i.drawHeldItem(c);
		}
	}
	
	public void createCollisionMap() {
		for(Block b : getBlockList()) {
			getCollisionMap().add(b.getHitBox());
		}
	}
	
	public void checkCollision(Player player) {
		checkBlockCollision(player);
		//checkItemCollision(player);
	}
	
	private void checkBlockCollision(Player player) {
		for(Block b: getBlockList()) {
			if(player.getHitbox().isColliding(b.getHitBox())) {
				b.onContact(player);
			}			
		}
	}
	
	public void checkItemCollision(Player player) {
		for(Item i : getItemList()) {
			if (player.getHitbox().isColliding(i.getHitBox())) {
				i.onContact(player, this);
				//getItemList().remove(i);
			}
		}
		
	}
	
	public void checkHeldItemCollision(Player player) {
		for(HeldItem i : heldItemList) {
			if (player.getHitbox().isColliding(i.getHitBox())) {
				i.onContact(player, this);
				//System.out.println("Collided with: "+ player.getName());
				//getItemList().remove(i);
			}
		}
		
	}
	
	public void updateBlockFrames() {
		blockFrameLoop += Gdx.graphics.getDeltaTime();
		if(blockFrameLoop > 0.25) {
			for(Block b: getBlockList()) {
				b.updateFrame();
			}
			
			blockFrameLoop = 0;
		}
	}
	
	private void addRandomItem() {
		ItemGenerator i = new ItemGenerator();
		Item temp = i.generateRandomItem(); //Created the random item
		System.out.println(temp.getUUIDasString());
		
		if(!temp.equals(null)) { //Make sure it is not null
			Boolean canAdd = true;
			for(Block b : getBlockList()) { //Goes through the block map can checks whether or not an item spawns on a block. 
				if(temp.getHitBox().isColliding(b.getHitBox()) && b.isSolid()) {
					System.out.println("P");
					canAdd = false; //If colliding with any blocks on the map, don't spawn.
					
				}
			}
			if(canAdd) {
				getItemList().add(temp); //Add to level list
				
				
			}
			
		}		
	}
	
	public void randomItemTimer() {
		timer += Gdx.graphics.getDeltaTime();
		
		if(getItemList().size()>maxNumItems) { //Timer cannot increase while there are max items spawned
			timer = 0;
		}
		
		if(timer >= 2f) {
			if(getItemList().size()<maxNumItems) {
				
			}
			timer = 0;
		}
	}
	
	public void addItem(Item i) {
		itemList.add(i);
	}
	
	public void removeItem(Item i) {
		itemList.remove(i);
		this.addToRemoveQueue(i);
	}
	
	public void addHeldItem(HeldItem i) {
		heldItemList.add(i);
		this.heldItemsToAdd.add(i);
	}
	
	public void removeHeldItem(HeldItem i) {
		heldItemList.remove(i);
		
	}

	public void doItemActions() {
		for (Item i : getItemList()) {
			i.doAction(this);
		}
		for (HeldItem i : heldItemList ) {
			i.doAction(this);
		}
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public List<CollisionBox> getCollisionMap() {
		return collisionMap;
	}

	protected void setCollisionMap(List<CollisionBox> collisionMap) {
		this.collisionMap = collisionMap;
	}

	public List<Block> getBlockList() {
		return blockList;
	}

	public void setBlockList(List<Block> blockList) {
		this.blockList = blockList;
	}
	
	protected void addToRemoveQueue(Item i){
		this.itemsToRemove.add(i);
	}
	
	public void removeFromRemoveQueue(Item i){
		this.itemsToRemove.remove(i);
	}
	
	public void removeFromAddQueue(Item i) {
		this.itemsToAdd.remove(i);
	}
	
	public List<Item> getAddQueue(){
		return itemsToAdd;
	}
	
	public List<Item> getRemoveQueue(){
		return itemsToRemove;
	}
	
	public List<HeldItem> getHeldItemsToAdd() {
		return heldItemsToAdd;
	}

	public void setHeldItemsToAdd(List<HeldItem> heldItemsToAdd) {
		this.heldItemsToAdd = heldItemsToAdd;
	}
	
	public List<HeldItem> getHeldItemList(){
		return heldItemList;
	}
	
}
