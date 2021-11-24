package com.mygdx.net;

public class Packet14AddHeldItem extends Packet {

	private String uuid;
	private float x,y;
	private int itemId;
	private String owner;
	
	public Packet14AddHeldItem(byte[] data) {//Used when retrieving data
		super(14);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.uuid = splitData[0];
		this.x = Float.parseFloat(splitData[1]);
		this.y = Float.parseFloat(splitData[2]);
		this.itemId = Integer.parseInt(splitData[3]);
		this.owner = splitData[4];
		
		
	}
	
	public Packet14AddHeldItem(String uuid, int itemId, float x, float y, String owner) {//Used when sending data from client in the original instance
		super(14);
		this.uuid = uuid;
		this.itemId = itemId;
		this.x = x;
		this.y = y;
		this.owner = owner;
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
	return (("14" + this.uuid + "," + this.x + ","+this.y + "," + this.itemId + "," + this.owner).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
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

public String getOwner() {
	return owner;
}

}
