package com.mygdx.net;

public class Packet25SendMessage extends Packet{

	private String usrName;
	private String message;

	public Packet25SendMessage(byte[] data) {//Used when retrieving data
		super(25);
		//Stored as: (Username, Max health, current health)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		this.message = splitData[1];
		
	}
	
	public Packet25SendMessage(String usrName, String message) {//Used when sending data from client in the original instance
		super(25);
		this.usrName = usrName;
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
	return (("25" + usrName + "," + message).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

public String getMessage() {
	return message;
}

}
