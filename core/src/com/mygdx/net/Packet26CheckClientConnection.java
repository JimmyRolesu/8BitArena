package com.mygdx.net;

public class Packet26CheckClientConnection extends Packet{

	String name;

	public Packet26CheckClientConnection(byte[] data) {//Used when retrieving data
		super(26);
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.name = splitData[0];
		
	}
	
	public Packet26CheckClientConnection(String name) {//Used when sending data from client in the original instance
		super(26);
		this.name = name;
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
	return (("26" + name).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return name;
}

}
