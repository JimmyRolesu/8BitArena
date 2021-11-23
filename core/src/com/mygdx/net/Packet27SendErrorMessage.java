package com.mygdx.net;

public class Packet27SendErrorMessage extends Packet{

	private String message;

	public Packet27SendErrorMessage(byte[] data) {//Used when retrieving data
		super(27);
		//Stored as: (Message)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.message = splitData[0];
		
	}
	
	public Packet27SendErrorMessage(String message) {//Used when sending data from client in the original instance
		super(27);
		this.message = message;
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
	return (("27" + message).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getMessage() {
	return message;
}

}
