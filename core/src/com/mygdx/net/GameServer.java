package com.mygdx.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mygdx.entities.Player;
import com.mygdx.entities.PlayerMP;
import com.mygdx.entities.PlayerStates;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.items.HeldItem;
import com.mygdx.items.HeldItem.HeldItemTypes;
import com.mygdx.items.HeldItem01BowArrow;
import com.mygdx.items.HeldItem02Bomb;
import com.mygdx.items.Item;
import com.mygdx.net.Packet.PacketTypes;
import com.mygdx.screens.MainGameScreen;
import com.mygdx.screens.MainGameScreenServer;

public class GameServer extends Thread{
	
	private static final int MIN_PORT_NO = 6502;
	private static final int MAX_PORT_NO = 6512;

	
	private DatagramSocket socket; //Socket where the datagram sockets will be sent through
	private MainGameScreenServer game;
	private Boolean running;
	private Boolean gameRunning;
	private int maxSize;
	private InetAddress thisIp;
	private int thisPort;
	
	//Queue<PlayerMP> connectedPlayers = new ConcurrentLinkedQueue<PlayerMP>();
	public List<PlayerMP> connectedPlayers = new CopyOnWriteArrayList<PlayerMP>();
	public List<Item> onScreenItems = new CopyOnWriteArrayList<Item>();
	
	public GameServer(MainGameScreenServer game, int maxSize) {
		this.game = game;
		running = true;
		gameRunning = false;
		this.maxSize = maxSize;
		thisPort = -1;
		
		for(int i = MIN_PORT_NO; i <= MAX_PORT_NO; i++) {
			Boolean portAvailable = portAvailable(i);
			if(portAvailable) {
				try {
					this.socket = new DatagramSocket(i); //With the port number, the socket now listens as opposed to just sending.
					thisPort = i;
				}catch(SocketException e) {
					e.printStackTrace();	
				}
				break;
			}
			else if (!portAvailable && i == MAX_PORT_NO) {
				System.out.println("Can't create a server.");
			}
		}
		
		thisIp = this.socket.getInetAddress();
		
	}
	
	
	public void run() { //Needs a run method since it is a thread
		while(running) {
					
			byte[] data = new byte[1024]; //This is the data that will be sent through the datagram packet. 1024 is the largest amount that can be sent.
			DatagramPacket packet = new DatagramPacket(data, data.length); //This is the actual packet that is sent across the socket. It contains the data and its length.
			
			try {
				socket.receive(packet); //This blocks (code stops at this line) until a packet is received
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			//System.out.println(connectedPlayers.size());
			
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) { //Finds out what type of packet is sent and deals with it accordingly
		String msg = new String(data).trim();
		PacketTypes type = Packet.lookupPackets(msg.substring(0,2));
		Packet p;
		switch(type) {
		default:
		case INVALID:
			System.out.println("INVALID PACKET!");
			break;
		case LOGIN:
			p = new Packet00Login(data);			
			System.out.println(address.getHostAddress() + ":" + port + " " + ((Packet00Login) p).getName());
			
			this.addConnection((Packet00Login)p, address, port);	
			break;
		case DISCONNECT:
			p = new Packet01Disconnect(data);
			System.out.println(((Packet01Disconnect)p).getName().trim() + " is disconnecting...");
			this.removeConnection((Packet01Disconnect)p);
			break;
		case MOVEMENT:
			//System.out.println("Player is Moving!");
			p = new Packet02Movement(data);
			//System.out.println("Player: " + ((Packet02Movement)p).getName() + " is at: " + ((Packet02Movement)p).getX() + "," + ((Packet02Movement)p).getY());
			this.movePlayers((Packet02Movement)p);
			break;
		case SENDHP:
			p = new Packet03SendHp(data);		
			this.sendHealthToClients((Packet03SendHp)p);
			break;
		case INITIALSERVERPING:
			p = new Packet04InitialServerPing(data);
			//System.out.println("Ping packet received!");
			
			if(connectedPlayers.size()<maxSize && !isNameAlreadyUsed(((Packet04InitialServerPing)p).getName())) {//Limit the amount of players that can connect to server & checks if name is already used.
				this.confirmConnectionWithClient((Packet04InitialServerPing)p, address, port);
			}
			else{
				this.sendServerFull(address, port);
			}
			break;
		case SERVERALIVE:
			p = new Packet05CheckServerConnection(data);
			this.confirmConnectionWithClient((Packet05CheckServerConnection)p, address, port);
			//System.out.println("Beep");
			break;
		case SENDSTATE:
			//System.out.println("Ping");
			p = new Packet06SendState(data);
			this.sendStateToClients(((Packet06SendState) p), address, port);
			break;
		case ADDITEM:
			p = new Packet07AddItem(data);
			//System.out.println(((Packet07AddItem)p).getUUID());
			break;
		case REMOVEITEM:
			p = new Packet08RemoveItem(data);
			//System.out.println("Removed: " + ((Packet08RemoveItem)p).getUUID());
			//this.removeGameItem(((Packet08RemoveItem)p));
			break;
		case REQUESTLOBBYINFO:
			p = new Packet09RequestLobbyInfo(data);
			//System.out.println("Lobby data request from: " + address+":"+port);
			
			p = new Packet10SendLobbyInfo(connectedPlayers.size(),maxSize);
			this.sendLobbyInfoToPlayer(((Packet10SendLobbyInfo)p), address, port);
			break;		
		case STARTGAME:
			this.startGame();
			break;
		case ADDHELDITEM:
			p = new Packet14AddHeldItem(data);
			System.out.println("Added");
			this.addHeldItem(((Packet14AddHeldItem)p));
			break;
		case MOVEMENTANTICHEAT:
			p = new Packet17AntiCheatMove(data);
			this.updateServerPlayerPosition(((Packet17AntiCheatMove)p));
			
			break;
		case USEITEMANTICHEAT:
			p = new Packet18AntiCheatUseItem(data);
			System.out.println("Item used by: " + ((Packet18AntiCheatUseItem)p).getName());
			this.useItem(((Packet18AntiCheatUseItem)p));
			break;
		case USESWORDANTICHEAT:
			p = new Packet19AntiCheatUseSword(data);
			//System.out.println("Attack");
			this.updateStates(((Packet19AntiCheatUseSword)p));
			break;
		case NOSWORDANTICHEAT:
			p = new Packet20AntiCheatNoSword(data);
			this.updateStates(((Packet20AntiCheatNoSword)p));
			break;
		case LATENCYPING:
			p = new Packet23LatencyPing(data);
			this.sendReturnLatencyPing(((Packet23LatencyPing)p), address, port);
			break;
		case SENDMESSAGE:
			p = new Packet25SendMessage(data);
			System.out.println(((Packet25SendMessage)p).getMessage());
			this.addMessage(((Packet25SendMessage)p));
			
			break;
		case CLIENTALIVE:
			p = new Packet26CheckClientConnection(data);
			
			this.setClientToConnected(((Packet26CheckClientConnection)p));
		}
	}
	
	private Boolean isNameAlreadyUsed(String name) {
		for(PlayerMP player: connectedPlayers) {
			if(player.getName().equals(name)) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
				return true;
			}
		}	
		return false;
	}

	private void setClientToConnected(Packet26CheckClientConnection p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setServerConnection(true);
				}
			}
	
		}else {
			System.out.println("Not a valid player");
			
		}
		
		
	}


	private void addMessage(Packet25SendMessage p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setMessage(p.getMessage());
					p.writeDataToClients(this);
				}
			}
	
		}else {
			System.out.println("Not a valid player");
		}
		
	}


	public InetAddress getIpAddress() {
		return thisIp;
	}
	
	public int getPort() {
		return thisPort;
	}

	private void sendServerFull(InetAddress address, int port) {
		Packet24IsServerFull p = new Packet24IsServerFull(true);
		sendData(p.getData(), address, port);
		
	}


	private void sendReturnLatencyPing(Packet23LatencyPing p, InetAddress address, int port) {
		//System.out.println("Latency");
		sendData(p.getData(), address, port);
		
	}


	private void useItem(Packet18AntiCheatUseItem p) {
		this.game.useItem(p.getName());
		
	}


	private void updateStates(Packet19AntiCheatUseSword p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setState(PlayerStates.ATTACKING); //Int 1 is for the attacking state
					//Packet06SendState p2 = new Packet06SendState(p.getName(), player.getState().getId());
					//p2.writeDataToClients(this);
				}
			}
	
		}else {
			System.out.println("Not a valid player");
		}
		
		
	}
	
	private void updateStates(Packet20AntiCheatNoSword p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName()) &&!player.getState().equals(PlayerStates.STUNNED)) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setState(PlayerStates.DEFAULT); //Int 1 is for the attacking state
					//Packet06SendState p2 = new Packet06SendState(p.getName(), player.getState().getId());
					//p2.writeDataToClients(this);
				}
			}
	
		}else {
			System.out.println("Not a valid player");
		}
		
		
	}


	private void sendStateToClients(Packet06SendState p, InetAddress address, int port) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setState(player.lookupState(p.getStateInt()));
					p.writeDataToClients(this);
					
				}
			}
	
		}else {
			System.out.println("Not a valid player");
		}
		
	}


	private void updateServerPlayerPosition(Packet17AntiCheatMove p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					this.game.calculatePlayerPosition(player, p.getDirection(), p);
					//Packet02Movement p2 = new Packet02Movement(p.getName(), player.getX(), player.getY(), player.getDirection());
					//p2.writeDataToClients(this);
				}
			}
	
		}else {
			System.out.println("Not a valid player");
		}
		
		
	}


	private void movePlayers(Packet02Movement p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setX(p.getX()); 
					player.setY(p.getY());
					player.setDirection(p.getDirection());
					player.getHitbox().makeBox();
					p.writeDataToClients(this);
				}
			}
	
		}else {
			System.out.println("Not a valid player");
		}
		
	}


	private void addHeldItem(Packet14AddHeldItem p) {
		
		HeldItemTypes type = HeldItem.lookupHeldItemType(p.getItemId());
		
		if(getPlayer(p.getOwner())!=null) {
			for(PlayerMP player : connectedPlayers) {
				if(player.getName().equals(p.getOwner())) {
					switch(type) {
					default:
					case BOWARROW:
						this.game.getLevel().getHeldItemList().add(new HeldItem01BowArrow(p.getX(), p.getY(), player.getDirection(), p.getUUID(), player.getName()));
						break;
					case BOMB:
						this.game.getLevel().getHeldItemList().add(new HeldItem02Bomb(p.getX(),p.getY(), player.getName()));
					}
					p.writeDataToClients(this);
				}
			}
		}
		
		
		
	}


	private void removeGameItem(Packet08RemoveItem p) {
		//System.out.println("Hi");
		for(Item item: game.getLevel().getItemList()) {
			if(item.getUUIDasString().equals(p.getUUID())) {
				game.getLevel().getItemList().remove(item);
			}
		}
		p.writeDataToClients(this);
		
	}


	private void startGame() {
		gameRunning = true;
		
	}


	private void sendLobbyInfoToPlayer(Packet10SendLobbyInfo p, InetAddress address, int port) {
		sendData(p.getData(), address, port);
		
	}


	private void confirmConnectionWithClient(Packet05CheckServerConnection p, InetAddress address, int port) {
		sendData(p.getData(), address, port);
		
	}


	private void confirmConnectionWithClient(Packet04InitialServerPing p, InetAddress address, int port) {
		sendData(p.getData(), address, port);
		
	}


	private void sendHealthToClients(Packet03SendHp p) {
		if(getPlayer(p.getName())!=null) {//Makes sure that the player is valid
			for(PlayerMP player: connectedPlayers) {
				if(player.getName().equals(p.getName())) {//Clients are identified by their user name. Must make sure that clients do not share the same name.
					player.setMaxHp(p.getMaxHp());
					player.setHp(p.getHp());
					p.writeDataToClients(this);
					//System.out.println(p.getName() + ":" + "Max HP = " + p.getMaxHp() + " " + "Current HP = " + p.getHp());
				}
			}

		}else {
			System.out.println("Not a valid player");
		}
		
		
	}


	public void addConnection(Packet00Login packet, InetAddress address, int port) {
		
		PlayerMP player = new PlayerMP(500, 400, ((Packet00Login) packet).getName(), address, port, false);
		
		Packet00Login packet2 = null; //New temporary packet created so the original isn't filled up.
		boolean alreadyConnected = false; //Ensure that the connection does not already exist
		for(PlayerMP p : this.connectedPlayers) {//Check player against all within all of players in server list.
			if(player.getName().equalsIgnoreCase(p.getName())) {//If the player is already in the game.
				if(p.getIpAddress() == null) {
					p.setIpAddress(player.getIpAddress());
				}
				
				if(p.getPort() == -1) {
					p.setPort(player.getPort());
				}
				
				alreadyConnected = true;
				
				
			}else {//If the player is not already connected
				//Packet00Login loginPacket = new Packet00Login(player.getName());
				sendData(packet.getData(),p.getIpAddress(), p.getPort());//Sends new login packet to each connected client
				
				packet2 = new Packet00Login(p.getName(), p.getX(), p.getY());
				
				sendData(packet2.getData(), player.getIpAddress(), player.getPort());//Sends data from each connected client to the one that has just connected.
				
			}
			
			
		}
		if(!alreadyConnected) {//If the player is not already connected
			this.connectedPlayers.add(player); //Adds player to local server list
			//packet.writeData(this);
		}
		
	}


	public void removeConnection(Packet01Disconnect p) {
		//PlayerMP player = getPlayer(p.getName());
		
		this.connectedPlayers.remove(getPlayer(p.getName()));//Removes the player from the server index.
		p.writeDataToClients(this);//Writes to all clients
		
	}
	
	public PlayerMP getPlayer(String name) {//Gets a player based on username
		for(PlayerMP player: this.connectedPlayers) {
			if(player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	
	public int getPlayerIndex(String name) {//Gets a player based on username
		int index = 0;
		for(PlayerMP player: this.connectedPlayers) {
			//System.out.println("Removing connections...");
			if(player.getName().equals(name)) {
				break;
			}
			index++;
		}
		return index;
	}


	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendDataToAllClients(byte[] data) { //List of all players connected to server
		for (PlayerMP p : connectedPlayers) {
			sendData(data, p.getIpAddress(), p.getPort());
		}
		
	}
	
	public void closeSocket() {
		socket.close();
	}


	public int getNumConnectedPlayers() {
		return connectedPlayers.size();
	}
	
	public Boolean isGameRunning() {
		return gameRunning;
	}
	
	public void setGameRunning(Boolean b) {
		gameRunning = b;
	}


	public void sendStartPacket() {
		System.out.println("Game about to start");
		Packet11StartGame p = new Packet11StartGame();
		p.writeDataToClients(this);
	}
	
	public List<PlayerMP> getPlayerList(){
		return connectedPlayers;
	}


	public Item getItem(String uuid) {
		for(Item item: onScreenItems) {
			if(item.getUUIDasString().equals(uuid)) {
				return item;
			}
		}
		return null;
	}


	/**
	 * Deprecated method. Used when using server to perform item collision.
	 * @param temp
	 */
	private void addGameItem(Item temp) {
		this.onScreenItems.add(temp);// Adds created item to the server's own list
		Packet07AddItem p = new Packet07AddItem(temp.getUUIDasString(), temp.getId(), temp.getX(), temp.getY());
		p.writeDataToClients(this);
		
		
	}


	/**
	 * Deprecated method. Used when using server to perform item collision.
	 * @param temp
	 */
	public void removeGameItem(Item temp) {
		this.onScreenItems.remove(temp);
		System.out.println("Removed: " + temp);
		Packet08RemoveItem p = new Packet08RemoveItem(temp.getUUIDasString());
		p.writeDataToClients(this);
	}
	
	public static boolean portAvailable(int port) {
		if(port < MIN_PORT_NO || port > MAX_PORT_NO) {
			throw new IllegalArgumentException("Invalid port");
		}
		
		DatagramSocket s = null;
		try {
			
			s = new DatagramSocket(port);
			s.setReuseAddress(true);
			return true;
			
		}catch (IOException e) {
			
		}finally {
			
			if(s != null) {
				s.close();
			}
		}
		
		return false;
	}
	
	/**
	 * Stops the thread safely.
	 */
	public void terminate() {
		running = false;
	}
	
	public Boolean isRunning() {
		return running;
	}
	
}
