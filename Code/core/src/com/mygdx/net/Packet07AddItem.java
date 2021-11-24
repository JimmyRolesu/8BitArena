package com.mygdx.net;

public class Packet07AddItem extends Packet{

	private String uuid;
	private float x,y;
	private int itemId;
	
	public Packet07AddItem(byte[] data) {//Used when retrieving data
		super(07);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.uuid = splitData[0];
		this.x = Float.parseFloat(splitData[1]);
		this.y = Float.parseFloat(splitData[2]);
		this.itemId = Integer.parseInt(splitData[3]);
		
		
	}
	
	public Packet07AddItem(String uuid, int itemId, float x, float y) {//Used when sending data from client in the original instance
		super(07);
		this.uuid = uuid;
		this.itemId = itemId;
		this.x = x;
		this.y = y;
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
	return (("07" + this.uuid + "," + this.x + ","+this.y + "," + this.itemId).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getUUID() {
	return uuid;
}

public float getX() {
	return x;
}

public float getY() {
	return y;
}

public int getItemId() {
	return itemId;
}

}
