package com.mygdx.net;

public class Packet19AntiCheatUseSword extends Packet{

	private String usrName;
	
	public Packet19AntiCheatUseSword(byte[] data) {//Used when retrieving data
		super(19);
		//Stored as: (Username)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
	}
	
	public Packet19AntiCheatUseSword(String usrName) {//Used when sending data from client in the original instance
		super(19);
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
	return (("19" + this.usrName).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

}
