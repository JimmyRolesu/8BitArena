package com.mygdx.net;

public class Packet24IsServerFull extends Packet{

	Boolean response;

	public Packet24IsServerFull(byte[] data) {//Used when retrieving data
		super(24);
		//Stored as: (response)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.response = Boolean.parseBoolean(splitData[0]);
		
	}
	
	public Packet24IsServerFull(Boolean response) {//Used when sending data from client in the original instance
		super(24);
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
	return (("24" + response).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public Boolean isFull() {
	return response;
}

}
