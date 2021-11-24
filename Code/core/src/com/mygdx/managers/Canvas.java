package com.mygdx.managers;


import java.net.InetAddress;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.entities.PlayerStates;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.HeldItem;
import com.mygdx.items.HeldItem.HeldItemTypes;
import com.mygdx.items.HeldItem01BowArrow;
import com.mygdx.items.HeldItem02Bomb;
import com.mygdx.items.Item;
import com.mygdx.items.Item.ItemTypes;
import com.mygdx.items.Item08Arrow;
import com.mygdx.level.Block;
import com.mygdx.level.Block.BlockTypes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Canvas {
	
	MultiplayerGame gm;
	
	//Animation[] playerAnime;
	
	Texture heart, halfHeart, emptyHeart, itemBox, floor, connectedIco, disconnectedIco;
	Texture standard, fire, fire2, head1, head2, sand, stairs, black, purple;
	Texture heart1, heart2, heartContainer, fairy1, fairy2, clock, rupee1, rupee2, bomb, bow, arrowUp, arrowDown, arrowRight, arrowLeft, boomerang, magicRod, candle, explosion1, explosion2, explosion3;
	
	//int stepLoops;
	
	private BitmapFont font;
	private FreeTypeFontGenerator fontGen;
	private FreeTypeFontParameter fontParam;
	private String title;
	private GlyphLayout glyph;
	private float glyphW, glyphH;
	
	//private TextureRegion Player;
	private TextureAtlas playerTextureAtlas, stunnedPlayerTextureAtlas;
	private TextureAtlas attackTextureAtlas;
	private TextureAtlas boomerangTextureAtlas;
	
	private TextureRegion[][] playerSprites, stunnedPlayerSprites;
	private TextureRegion[][] attackSprites;
	private TextureRegion[] boomerangSprites;
	
	private PlayerStates tempState;
	
	public Canvas(MultiplayerGame game) {
		gm = game;
		textureInitilise();
		playerTextureInitilise();
		fontInitilise();
		initiliseBlocks();
		initiliseItems();
		
	}
	
	private void fontInitilise() {
		fontGen = new FreeTypeFontGenerator(Gdx.files.internal("eightbit.ttf"));
		fontParam = new FreeTypeFontParameter();
		fontParam.size = MultiplayerGame.TILESIZE/2;
		fontParam.color = Color.WHITE;
		font = fontGen.generateFont(fontParam);
		glyph = new GlyphLayout();
	}

	private void textureInitilise() {
		heart = new Texture("full.png");
		halfHeart = new Texture("half.png");
		emptyHeart = new Texture("empty.png");
		itemBox = new Texture("itemBox.png");
		floor = new Texture("smallFloor.png");
		connectedIco = new Texture("ConnectedIcon.png");
		disconnectedIco = new Texture("DisconnectedIcon.png");
	}
	
	private void initiliseBlocks() {
		standard = new Texture("blocks/StandardBlock.png");
		fire = new Texture("blocks/Fire1.png");
		fire2 = new Texture("blocks/Fire2.png");
		head1 = new Texture("blocks/HeadBlock1.png");
		head2 = new Texture("blocks/HeadBlock2.png");
		sand = new Texture("blocks/SandBlock.png");
		stairs = new Texture("blocks/StairBlock.png");
		black = new Texture("blocks/BlackBlock.png");
		purple = new Texture("blocks/PurpleBlock.png");
	}
	
	private void initiliseItems() {
		heartContainer = new Texture("items/HeartContainerItem.png");
		fairy1 = new Texture("items/Fairy1Item.png");
		fairy2 = new Texture("items/Fairy2Item.png");
		clock = new Texture("items/ClockItem.png");
		rupee1 = new Texture("items/Rupee1Item.png");
		rupee2 = new Texture("items/Rupee2Item.png");
		bomb = new Texture("items/BombItem.png");
		bow = new Texture("items/BowItem.png");
		arrowUp = new Texture("items/ArrowUpItem.png");
		arrowDown = new Texture("items/ArrowDownItem.png");
		arrowRight = new Texture("items/ArrowRightItem.png");
		arrowLeft = new Texture("items/ArrowLeftItem.png");
		boomerang = new Texture("items/BoomerangItem.png");
		magicRod = new Texture("items/MagicRodItem.png");
		candle = new Texture("items/CandleItem.png");
		explosion1 = new Texture("blocks/ExplosionBlock1.png");
		explosion2 = new Texture("blocks/ExplosionBlock2.png");
		explosion3 = new Texture("blocks/ExplosionBlock3.png");
		heart1 = new Texture("items/Heart1Item.png");
		heart2 = new Texture("items/Heart2Item.png");
		
		boomerangTextureAtlas = new TextureAtlas("items/BoomerangSheetItem.atlas");
		
		boomerangSprites = new TextureRegion[8];
		boomerangSprites[0] = boomerangTextureAtlas.findRegion("Boomerang1Item");
		boomerangSprites[1] = boomerangTextureAtlas.findRegion("Boomerang2Item");
		boomerangSprites[2] = boomerangTextureAtlas.findRegion("Boomerang3Item");
		boomerangSprites[3] = boomerangTextureAtlas.findRegion("Boomerang4Item");
		boomerangSprites[4] = boomerangTextureAtlas.findRegion("Boomerang5Item");
		boomerangSprites[5] = boomerangTextureAtlas.findRegion("Boomerang6Item");
		boomerangSprites[6] = boomerangTextureAtlas.findRegion("Boomerang7Item");
		boomerangSprites[7] = boomerangTextureAtlas.findRegion("Boomerang8Item");
		
		
	}

	/**
	 * Initilises a new player texture atlas that is contained as a 2d array. The first dimension of the array determines the direction the player is facing,
	 * the second dimension is for the second walking frame.
	 */
	private void playerTextureInitilise() {
		playerTextureAtlas = new TextureAtlas("PlayerSprites.atlas");
	
		playerSprites = new TextureRegion[4][2];
		//Sprites are 16*16 in size
		playerSprites[0][0]= playerTextureAtlas.findRegion("0 LinkDown1");
		playerSprites[0][1]= playerTextureAtlas.findRegion("0 LinkDown2");
		playerSprites[1][0]= playerTextureAtlas.findRegion("1 LinkUp1");
		playerSprites[1][1]= playerTextureAtlas.findRegion("1 LinkUp2");
		playerSprites[2][0]= playerTextureAtlas.findRegion("2 LinkRight1");
		playerSprites[2][1]= playerTextureAtlas.findRegion("2 LinkRight2");
		playerSprites[3][0]= playerTextureAtlas.findRegion("3 LinkLeft2");
		playerSprites[3][1]= playerTextureAtlas.findRegion("3 LinkLeft1");
		
		
		stunnedPlayerTextureAtlas = new TextureAtlas("StunnedPlayerSprites.atlas");
		
		stunnedPlayerSprites = new TextureRegion[4][2];
		//Sprites are 16*16 in size
		stunnedPlayerSprites[0][0]= stunnedPlayerTextureAtlas.findRegion("0 LinkDown1"); //Share the same atlas since sprites are just recoloured
		stunnedPlayerSprites[0][1]= stunnedPlayerTextureAtlas.findRegion("0 LinkDown2");
		stunnedPlayerSprites[1][0]= stunnedPlayerTextureAtlas.findRegion("1 LinkUp1");
		stunnedPlayerSprites[1][1]= stunnedPlayerTextureAtlas.findRegion("1 LinkUp2");
		stunnedPlayerSprites[2][0]= stunnedPlayerTextureAtlas.findRegion("2 LinkRight1");
		stunnedPlayerSprites[2][1]= stunnedPlayerTextureAtlas.findRegion("2 LinkRight2");
		stunnedPlayerSprites[3][0]= stunnedPlayerTextureAtlas.findRegion("3 LinkLeft2");
		stunnedPlayerSprites[3][1]= stunnedPlayerTextureAtlas.findRegion("3 LinkLeft1");
		
		//Attack Sprites are 48*48 in size
		
		attackTextureAtlas = new TextureAtlas("PlayerAttacks.atlas");
		
		attackSprites = new TextureRegion[4][3];
		attackSprites[0][0] = attackTextureAtlas.findRegion("0 downAttack1");
		attackSprites[1][0] = attackTextureAtlas.findRegion("1 upAttack1");
		attackSprites[2][0] = attackTextureAtlas.findRegion("3 rightAttack1");
		attackSprites[3][0] = attackTextureAtlas.findRegion("4 leftAttack1");
		
		
	}

	public void drawName(String name, float x, float y) {
		title = name;
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, (float)x - glyphW/2, (float) y+(MultiplayerGame.TILESIZE/2 + glyphH));
	}
	
	public void drawMessage(String message, float x, float y) {
		title = message;
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, (float)x - glyphW/2, (float) y-(MultiplayerGame.TILESIZE - glyphH));
	}
	
	public void drawConnectionLost(float timer) {
		title = "Connection to server lost!";
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT/2 - glyphH/2);
		title = "Returning to Main Menu in: " + Math.round(timer);
		glyph.setText(font, title);
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT/2 - glyphH/2 - glyphH*2);
	}
	
	public void drawLobby(int numConnectedPlayers, int maxPlayers) {
		title = numConnectedPlayers+"/"+maxPlayers+" are connected.";
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT/2 - glyphH/2);
		
		
	}

	public void drawServer(int port, List<PlayerMP> players, int maxPlayers, List<String> messages) {
		
		title = "The server has been started on port: "+port;
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE*2 - glyphH/2);


		title = "Connected Players "+"("+players.size()+"/"+maxPlayers+")";
		GlyphLayout glyph2 = new GlyphLayout();
		glyph2.setText(font, title);
		//float glyph2W = glyph2.width;
		//float glyph2H = glyph2.height;
		font.draw(gm.batch, title, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE*2 - glyphH/2 - glyphH*2);
		float currentY = MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE*2 - glyphH/2 - glyphH*2;
		for(PlayerMP p : players) {
			currentY -= glyphH*2;
			font.draw(gm.batch, "-"+p.getName() + " ("+p.getIpAddress().toString()+")" + "("+(int)p.getX()+","+(int)p.getY()+")", MultiplayerGame.WIDTH/2 - glyphW/2, currentY);
		}
		
		float messageY = 0 + glyphH*2;
		for(String s : messages) {
			font.draw(gm.batch, s, MultiplayerGame.WIDTH/2 - glyphW/2, messageY);
			messageY += glyphH*2;
			
		}
	}
	
	public void drawNoServerFound() {
		title = "Looking for server...";
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT/2 - glyphH/2);
	}
	
	public void drawErrorScreen(String errorMessage) {
		title = "ERROR: "+errorMessage;
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT/2 - glyphH/2);
		
	}

	public void drawGameOverScreen(String winner) {
		title = "Game Over, the winner is " + winner + "\n\n" + "Press ENTER to return to the main menu!";
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT/2 - glyphH/2);
		
	}

	public void drawEnemyHp(int hp, int maxHp, float x, float y) {
		int noFull = hp/2; //Number of full hearts
        int noHalf = hp%2; //Number of half hearts
        int noEmpty = maxHp/2 - (noFull+noHalf);
        int total = noFull+noHalf+noEmpty;//Stores total about of heart sprites drawn
        
       
        float offset = 0; //Places hearts away from one another
        float finalOffset = 0f;
        //System.out.println(noFull+" "+noHalf+" "+noEmpty);
        
        for(int i=0; i<noFull; i++){
            offset = offset + (MultiplayerGame.TILESIZE + MultiplayerGame.TILESIZE/10)/2;
        }

        for(int i=0; i<noHalf; i++){
            offset = offset + (MultiplayerGame.TILESIZE + MultiplayerGame.TILESIZE/10)/2;
        }

        for(int i=0; i<noEmpty; i++){
            offset = offset + (MultiplayerGame.TILESIZE + MultiplayerGame.TILESIZE/10)/2;
        }
        
       finalOffset = offset;
       offset = x - finalOffset/2;
        //------------------------------------------------------------------------------------------------------------------------------

        for(int i=0; i<noFull; i++){
        	gm.batch.draw(heart, offset, y + MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2);
            offset = offset + (MultiplayerGame.TILESIZE + MultiplayerGame.TILESIZE/10)/2;
        }

        for(int i=0; i<noHalf; i++){
        	gm.batch.draw(halfHeart, offset, y + MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2);
            offset = offset + (MultiplayerGame.TILESIZE + MultiplayerGame.TILESIZE/10)/2;
        }

        for(int i=0; i<noEmpty; i++){
        	gm.batch.draw(emptyHeart, offset, y + MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2);
            offset = offset + (MultiplayerGame.TILESIZE + MultiplayerGame.TILESIZE/10)/2;
        }
        //System.out.println("Actual: "+offset);
        //System.out.println("Final: " +finalOffset);
	}
	
	/**
	 * Draws all HUD elements that appear at the top of the screen.
	 * @param hp
	 * @param maxHp
	 */
	public void drawHUD(int hp, int maxHp, String name, Boolean connected) {
		drawHP(hp, maxHp);
		drawHUDName(name);
		drawHUDConnection(connected);
		//drawItemBox();
	}
	
	private void drawHUDConnection(Boolean connected) {
		if (connected) {
			gm.batch.draw(connectedIco, MultiplayerGame.WIDTH/2 - MultiplayerGame.TILESIZE/2, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE*2 + MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);
		}
		else {
			gm.batch.draw(disconnectedIco, MultiplayerGame.WIDTH/2 - MultiplayerGame.TILESIZE/2, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE*2 + MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);
		}
	}
	
	private void drawHUDName(String name) {
		title = name;
		glyph.setText(font, title);
		glyphW = glyph.width;
		font.draw(gm.batch, glyph, (float)MultiplayerGame.WIDTH -glyphW - MultiplayerGame.TILESIZE, (float) MultiplayerGame.HEIGHT-MultiplayerGame.TILESIZE);
	}
	
	/**
	 * Draws the number of hearts the player has remaining.
	 * @param hp
	 * @param maxHp
	 */
	private void drawHP(int hp, int maxHp) {
		
        int noFull = hp/2; //Number of full hearts
        int noHalf = hp%2; //Number of half hearts
        int noEmpty = maxHp/2 - (noFull+noHalf);

        int offset = MultiplayerGame.TILESIZE/10; //Places hearts away from one another
        //System.out.println(noFull+" "+noHalf+" "+noEmpty);

        for(int i=0; i<noFull; i++){
        	gm.batch.draw(heart, offset, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2);
            offset = offset + (MultiplayerGame.TILESIZE + 10)/2;
        }

        for(int i=0; i<noHalf; i++){
        	gm.batch.draw(halfHeart, offset, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2);
            offset = offset + (MultiplayerGame.TILESIZE + 10)/2;
        }

        for(int i=0; i<noEmpty; i++){
        	gm.batch.draw(emptyHeart, offset, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE/2, MultiplayerGame.TILESIZE/2);
            offset = offset + (MultiplayerGame.TILESIZE + 10)/2;
        }
	}
	
	private void drawItemBox() {
		
		gm.batch.draw(itemBox, MultiplayerGame.WIDTH, MultiplayerGame.HEIGHT - MultiplayerGame.TILESIZE);
	}
	
	public void drawFloor() {
		
		int xOffset = 0;
		int yOffset = MultiplayerGame.HUDSPACE;
		for (int i=0; i<MultiplayerGame.YSPLIT;i++) {//y
			for (int j = 0; j<MultiplayerGame.XSPLIT; j++) {//x
				gm.batch.draw(floor, xOffset, yOffset, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);
			xOffset += MultiplayerGame.TILESIZE;
			}
		xOffset = 0;
		yOffset -= MultiplayerGame.TILESIZE;
		}
	}
	
	/**
	 * Draws player to the screen.
	 * @param x
	 * @param y
	 */
	public void drawPlayer(float x, float y, int direction, int count, PlayerStates playerState) {
		//ArrayList<Texture> sprites;
		switch(playerState) {
		case DEFAULT:
			gm.batch.draw(playerSprites[direction][count], x-(MultiplayerGame.TILESIZE/2), y-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);
			break;
		case ATTACKING:
			gm.batch.draw(attackSprites[direction][0], x-(MultiplayerGame.TILESIZE*3)/2, y-(MultiplayerGame.TILESIZE*3)/2, MultiplayerGame.TILESIZE*3, MultiplayerGame.TILESIZE*3);//Attack sprites are larger 48*48 images so tilesize must be multiplied by 3.
			break;
		case STUNNED:
			gm.batch.draw(stunnedPlayerSprites[direction][count], x-(MultiplayerGame.TILESIZE/2), y-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);
			break;
		}
		
		drawCentre(x,y); //Dot for debugging player centre
	}
	
	public void drawBlock(BlockTypes type, Block b) {
		switch(type) {
		default:
		case INVALID:
			break;
		case STANDARD:
			gm.batch.draw(standard, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
		case FIRE:
			if(b.getFrame() == 0) {
				gm.batch.draw(fire, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			}
			else {
				gm.batch.draw(fire2, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			}
			break;
		case HEAD1:
			gm.batch.draw(head1, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
		case HEAD2:
			gm.batch.draw(head2, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
		case SAND:
			gm.batch.draw(sand, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
		case STAIRS:
			gm.batch.draw(stairs, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
		case BLACK:
			gm.batch.draw(black, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
		case PURPLE:
			gm.batch.draw(purple, b.getX()-(b.getWidth()/2), b.getY()-(b.getHeight()/2), b.getWidth(), b.getHeight());
			break;
			
		}
		drawCentre(b.getX(), b.getY());
	}

	private void drawCentre(float x, float y) {
		//gm.batch.draw(this.disconnectedIco, x-5, y-5, 10, 10);
		
	}

	public void drawItem(ItemTypes type, Item item) {
		switch(type) {
		default:
		case INVALID:
			break;
		case HEART:
			if(item.getFrame()==0) {
				gm.batch.draw(heart1, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			}
			else {
				gm.batch.draw(heart2, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			}
			break;
		case HEARTCONTAINER:
			gm.batch.draw(heartContainer, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			break;
		case FAIRY:
			if(item.getFrame()==0) {
				gm.batch.draw(fairy1, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			}
			else {
				gm.batch.draw(fairy2, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			}			
			break;
		case CLOCK:
			gm.batch.draw(clock, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			break;
		case BOW:
			gm.batch.draw(bow, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			break;
		case BOMB:
			gm.batch.draw(bomb, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			break;
		case BOOMERANG:
			gm.batch.draw(boomerang, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			break;
//		case ARROW:
//			switch(((Item08Arrow)item).getDirection()) {
//			case 0:
//				gm.batch.draw(arrowDown, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
//				break;
//			case 1:
//				gm.batch.draw(arrowUp, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
//				break;
//			case 2:
//				gm.batch.draw(arrowRight, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
//				break;
//			case 3:
//				gm.batch.draw(arrowLeft, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
//				break;
//			}
//			
//			
//			break;	
		}
		
		
	}

	public void drawHeldItem(HeldItemTypes type, HeldItem item) {
		switch(type) {
		default:
		case BOWARROW:
			
			switch(((HeldItem01BowArrow)item).getDirection()) {
			case 0:
				gm.batch.draw(arrowDown, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
				break;
			case 1:
				gm.batch.draw(arrowUp, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
				break;
			case 2:
				gm.batch.draw(arrowRight, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
				break;
			case 3:
				gm.batch.draw(arrowLeft, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
				break;
			}
			
			
			break;	
		case BOMB:
			if(((HeldItem02Bomb)item).isExploded()) {
				float i = ((HeldItem02Bomb)item).getExplosionTimer();
				float j = ((HeldItem02Bomb)item).getExplosionTime();
				if(i > j*3/4) {
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2), item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Middle
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Right
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Left
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Top-Right
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Top-Left
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Bottom-Right
					gm.batch.draw(explosion1, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Bottom-Left
					
				}
				else if(i > j*2/4) {
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2), item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Middle
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Right
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Left
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Top-Right
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Top-Left
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Bottom-Right
					gm.batch.draw(explosion2, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Bottom-Left
					}
				else {
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2), item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Middle
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Right
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, item.getY()-(MultiplayerGame.TILESIZE/2), MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Left
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Top-Right
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Top-Left
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2)+MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Bottom-Right
					gm.batch.draw(explosion3, item.getX()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE/2, item.getY()-(MultiplayerGame.TILESIZE/2)-MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE, MultiplayerGame.TILESIZE);//Bottom-Left
					}
				
			}
			else {
				gm.batch.draw(bomb, item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());
			}
			
			break;
		case BOOMERANG:
			gm.batch.draw(boomerangSprites[item.returnFrame()], item.getX()-(item.getWidth()/2), item.getY()-(item.getHeight()/2), item.getWidth(), item.getHeight());	
			break;
		}
		
	}

	public void drawH2PScreen() {
		title = "How to Play:";
		glyph.setText(font, title);
		glyphW = glyph.width;
		glyphH = glyph.height;
		font.draw(gm.batch, glyph, MultiplayerGame.WIDTH/2 - glyphW/2, MultiplayerGame.HEIGHT - glyphH/2);
		
	}


}
