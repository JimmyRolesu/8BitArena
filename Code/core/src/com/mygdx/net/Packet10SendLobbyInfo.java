package com.mygdx.net;

public class Packet10SendLobbyInfo extends Packet{

	private int numConnectedPlayers, maxPlayers;
	
	
	public Packet10SendLobbyInfo(byte[] data) {//Used when retrieving data
		super(10);
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.numConnectedPlayers = Integer.parseInt(splitData[0]);
		this.maxPlayers = Integer.parseInt(splitData[1]);
	}
	
	public Packet10SendLobbyInfo(int numConnectedPlayers, int maxPlayers) {//Used when sending data from client in the original instance
		super(10);
		this.numConnectedPlayers = numConnectedPlayers;
		this.maxPlayers = maxPlayers;
		
	}

@Override
public void writeDataToServer(GameClient client) {
	client.sendData(getData());
}

@Override
public void writeDataToClients(GameServer server) {
	server.sendDataToAllClients(getData());
}

@Override
public byte[] getData() {
	return (("10" + numConnectedPlayers + "," + maxPlayers).getBytes());
}

public int getNumConnectedPlayers() {
	return numConnectedPlayers;
}

public int getMaxPlayers() {
	return maxPlayers;
}
	

}
