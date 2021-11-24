package com.mygdx.net;

public class Packet21ChangeLevel extends Packet{
	private String level;
	
	public Packet21ChangeLevel(byte[] data) {//Used when retrieving data
		super(21);
		//Stored as: (Username)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.level = splitData[0];
	}
	
	public Packet21ChangeLevel(String level) {//Used when sending data from client in the original instance
		super(21);
		this.level = level;
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
	return (("21" + this.level).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getLevelName() {
	return level;
}

}
