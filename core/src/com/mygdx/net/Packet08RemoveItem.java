package com.mygdx.net;

public class Packet08RemoveItem extends Packet{


	private String uuid;
	
	public Packet08RemoveItem(byte[] data) {//Used when retrieving data
		super(8);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.uuid = splitData[0];
		
		
	}
	
	public Packet08RemoveItem(String uuid) {//Used when sending data from client in the original instance
		super(8);
		this.uuid = uuid;
		
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
	return (("08" + this.uuid).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getUUID() {
	return uuid;
}


}
