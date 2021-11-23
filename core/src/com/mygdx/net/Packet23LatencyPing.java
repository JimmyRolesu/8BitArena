package com.mygdx.net;

public class Packet23LatencyPing extends Packet{

	String message;

	public Packet23LatencyPing(byte[] data) {//Used when retrieving data
		super(23);
		//Stored as: (Username, Max health, current health)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.message = splitData[0];
		
	}
	
	public Packet23LatencyPing() {//Used when sending data from client in the original instance
		super(23);
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
	return (("23" + "ping").getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

}
