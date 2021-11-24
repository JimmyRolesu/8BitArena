package com.mygdx.net;

public class Packet05CheckServerConnection extends Packet{//The packet sent at movement
	
	String message;

	public Packet05CheckServerConnection(byte[] data) {//Used when retrieving data
		super(05);
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.message = splitData[0];
		
	}
	
	public Packet05CheckServerConnection() {//Used when sending data from client in the original instance
		super(05);
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
	return (("05" + "," + "ping").getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

}
