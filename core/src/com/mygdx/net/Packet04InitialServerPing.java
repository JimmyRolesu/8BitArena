package com.mygdx.net;

public class Packet04InitialServerPing extends Packet{//The packet sent at movement
	
	private String usrName;

	public Packet04InitialServerPing(byte[] data) {//Used when retrieving data
		super(04);
		//Stored as: (Username, Max health, current health)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		
	}
	
	public Packet04InitialServerPing(String usrName) {//Used when sending data from client in the original instance
		super(04);
		this.usrName = usrName;
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
	return (("04" + usrName).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

}
