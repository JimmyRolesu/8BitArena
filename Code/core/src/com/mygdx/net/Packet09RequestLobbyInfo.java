package com.mygdx.net;

public class Packet09RequestLobbyInfo extends Packet{

	
	public Packet09RequestLobbyInfo(byte[] data) {//Used when retrieving data
		super(9);
		
	}
	
	public Packet09RequestLobbyInfo() {//Used when sending data from client in the original instance
		super(9);
		
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
	return (("09").getBytes());
}
	

}
