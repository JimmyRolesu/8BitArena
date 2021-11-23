package com.mygdx.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.UnknownHostException;
import java.util.Date;

import com.mygdx.entities.PlayerMP;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.net.Packet.PacketTypes;
import com.mygdx.screens.MainGameScreen;
import com.mygdx.screens.MainGameScreenClient;

public class GameClient extends Thread{ //Runs in a seperate thread to not put a load on the game.
	
	private static final int MIN_PORT_NO = 6502;
	private static final int MAX_PORT_NO = 6512;

	private InetAddress ipAddress; //IP of server
	private DatagramSocket socket; //Socket where the datagram sockets will be sent through
	private MainGameScreenClient game;
	private Boolean running;
	private Boolean sendingLatencyPacket;
	private long msSend, msReceive, latency;
	private int serverPort;
	
	public GameClient(MainGameScreenClient game, String ipAddress) {
		this.game = game;
		running = true;
		sendingLatencyPacket = false;
		msSend = 0;
		msReceive = 0;
		latency = 0;
		serverPort = MIN_PORT_NO;
		try {
		this.socket = new DatagramSocket(); //Setting variables up
		
		this.ipAddress = InetAddress.getByName(ipAddress);
		}catch(SocketException e) {
			e.printStackTrace();
		}catch(java.net.UnknownHostException e) {
			e.printStackTrace();
		}
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
			//System.out.println("Server: "+ new String(packet.getData())); //Prints the data received as a string
			
			
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
			loginPlayer((Packet00Login)p, address, port);
			break;
		case DISCONNECT:
			p = new Packet01Disconnect(data);
			System.out.println(((Packet01Disconnect)p).getName() + " is disconnecting...");
			game.removePlayer(((Packet01Disconnect)p).getName());//Removes the player from the internal client list
			break;
		case MOVEMENT:
			//System.out.println("Player is Moving!");
			p = new Packet02Movement(data);
			//System.out.println("Player: " + ((Packet02Movement)p).getName() + " is at: " + ((Packet02Movement)p).getX() + "," + ((Packet02Movement)p).getY());
			this.movePlayers((Packet02Movement)p);
			break;
		case SENDHP:
			p = new Packet03SendHp(data);	
			this.setHp((Packet03SendHp)p);
			break;
		case INITIALSERVERPING:
			game.setConnectedToServer(true);
			game.setInitialConnection();
			game.connectLocalPlayer();
			break;
		case SERVERALIVE:
			game.setConnectedToServer(true);
			break;
		case SENDSTATE:
			p = new Packet06SendState(data);
			//System.out.println(((Packet06SendState) p).getName() +" state is: "+((Packet06SendState) p).getStateInt());
			this.setStates(((Packet06SendState) p));
			break;
		case ADDITEM:
			p = new Packet07AddItem(data);
			System.out.println(((Packet07AddItem)p).getUUID());
			this.addItem((Packet07AddItem)p);
			break;
		case REMOVEITEM:
			//System.out.println("Removed");
			p = new Packet08RemoveItem(data);
			this.removeItem(((Packet08RemoveItem)p));
			break;
		case SENDLOBBYINFO:
			p = new Packet10SendLobbyInfo(data);
			this.updateLobbyInfo(((Packet10SendLobbyInfo)p));
			break;
		case STARTGAME:
			this.startGame();
			break;
		case STOPGAME:
			//If the game ever needs to stop for any unspecific reason.
			break;
		case SENDPLAYERITEM:
			p = new Packet13SendPlayerItem(data);
			this.updatePlayerItems(((Packet13SendPlayerItem)p));
			break;
		case ADDHELDITEM:
			p = new Packet14AddHeldItem(data);
			this.addHeldItems(((Packet14AddHeldItem)p));
			break;
		case SETDEAD:
			//System.out.println("Got");
			p = new Packet15SetDead(data);
			this.setDeadPlayer((Packet15SetDead)p);
			break;
		case SENDGAMEOVER:
			p = new Packet16SendGameOver(data);
			this.setGameOver(((Packet16SendGameOver)p));
			break;
		case MOVEMENTANTICHEAT:
			p = new Packet17AntiCheatMove(data);
			//this.movePlayersInterpolated(((Packet17AntiCheatMove)p));
			break;
		case CHANGELEVEL:
			p = new Packet21ChangeLevel(data);
			this.changeLevel(((Packet21ChangeLevel)p));
			break;
		case RESETPLAYER:
			p = new Packet22ResetPlayer(data);
			System.out.println("Reset");
			this.resetPlayers(((Packet22ResetPlayer)p));
			break;
		case LATENCYPING:
			this.getLatencyPacket();
			break;
		case SERVERFULL:
			System.out.println(serverPort);
			if (serverPort < MAX_PORT_NO) {
				serverPort++;
			} else {
				serverPort = MIN_PORT_NO;
			}
			break;
		case SENDMESSAGE:
			p = new Packet25SendMessage(data);
			System.out.println(((Packet25SendMessage)p).getName() + " said: " +((Packet25SendMessage)p).getMessage());
			this.setMessage(((Packet25SendMessage)p));
			break;
		case CLIENTALIVE:
			//System.out.println("Are you ok?");
			p = new Packet26CheckClientConnection(data);
			p.writeDataToServer(this);
			break;
		case ERRORMESSAGE:
			p = new Packet27SendErrorMessage(data);
			this.setErrorScreen(((Packet27SendErrorMessage)p).getMessage());
			System.out.println("Error");
			break;
		}

	}
	
	private void setErrorScreen(String message) {
		this.game.setErrorScreen(message);
	}


	private void setMessage(Packet25SendMessage p) {
		this.game.setMessage(p.getName(), p.getMessage());
		
	}


	private void movePlayersInterpolated(Packet17AntiCheatMove p) {
		this.game.movePlayerInterpolated(p.getName(), p.getDirection(), p.getTimeDone());
		
	}


	private void resetPlayers(Packet22ResetPlayer p) {
		this.game.resetPlayer(p.getName());
		
	}


	private void changeLevel(Packet21ChangeLevel p) {
		this.game.changeLevel(p.getLevelName());
		
	}


	private void setGameOver(Packet16SendGameOver p) {
		this.game.setGameOver(p.getWinner());
		
	}


	private void setDeadPlayer(Packet15SetDead p) {
		this.game.setDeadPlayer(p.getUsrName());
		
	}


	private void addHeldItems(Packet14AddHeldItem p) {
		this.game.addHeldItems(p.getOwner(), p.getX(), p.getY(), p.getUUID(), p.getItemId());
		
	}


	private void updatePlayerItems(Packet13SendPlayerItem p) {
		this.game.updatePlayerItems(p.getName(), p.getItemId());
		
	}


	private void startGame() {
		this.game.startGame();
		
	}


	private void updateLobbyInfo(Packet10SendLobbyInfo p) {
		this.game.updateLobbyInfo(p.getNumConnectedPlayers(), p.getMaxPlayers());
		
	}


	private void removeItem(Packet08RemoveItem p) {
		this.game.removeItem(p.getUUID());
		
	}
	
	


	private void addItem(Packet07AddItem p) {
		this.game.addItem(p.getUUID(), p.getItemId(), p.getX(), p.getY());
		
	}


	private void setStates(Packet06SendState p) {
		this.game.setState(p.getName(), p.getStateInt());
		
	}


	private void setHp(Packet03SendHp p) {
		this.game.setHp(p.getName(), p.getMaxHp(), p.getHp());
		
	}


	private void loginPlayer(Packet00Login p, InetAddress address, int port) {
		System.out.println(address.getHostAddress() + ":" + port + " " + ((Packet00Login) p).getName() + " has joined");
		PlayerMP player = new PlayerMP(p.getX(), p.getY(), ((Packet00Login) p).getName(), address, port, false, this);
		game.addPlayer(player);	//Adds player to the client game
		game.printPlayerList();
	}
	
	private void movePlayers(Packet02Movement p) {
		this.game.movePlayer(p.getName(), p.getX(), p.getY(), p.getDirection());
		
	}


	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, serverPort);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendLatencyPacket() {
		 if(!sendingLatencyPacket) {
			 sendingLatencyPacket = true;
			 
			 Date now = new Date();
			 msSend = now.getTime();
			 
			 Packet23LatencyPing p = new Packet23LatencyPing();
			 p.writeDataToServer(this);
		 }
		 
		
	}


	private void getLatencyPacket() {
		Date now = new Date();
		msReceive = now.getTime();
		
		if(!(msSend == 0)) {
			latency = msReceive - msSend;
			msSend = 0;
			msReceive = 0;
		}
		sendingLatencyPacket = false;
		//System.out.println(latency + "ms");
		
	}
	
	/**
	 * Stops the thread safely.
	 */
	public void terminate() {
		running = false;
	}
	
	public void closeSocket() {
		socket.close();
	}
	
	public Boolean isRunning() {
		return running;
	}
	
}
