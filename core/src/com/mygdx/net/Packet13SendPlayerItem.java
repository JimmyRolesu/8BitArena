package com.mygdx.net;

public class Packet13SendPlayerItem extends Packet{

	private String usrName;
	private int itemId;
	
	
	public Packet13SendPlayerItem(byte[] data) {//Used when retrieving data
		super(13);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		this.itemId = Integer.parseInt(splitData[1]);
			
	}
	
	public Packet13SendPlayerItem(String usrName, int itemId) {//Used when sending data from client in the original instance
		super(13);
		this.usrName = usrName;
		this.itemId = itemId;
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
	return (("13" + this.usrName + "," + this.itemId).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

public int getItemId() {
	return itemId;
}

}
