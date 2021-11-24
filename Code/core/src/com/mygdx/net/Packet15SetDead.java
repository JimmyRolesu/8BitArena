package com.mygdx.net;

public class Packet15SetDead extends Packet{

	private String usrName;
	
	public Packet15SetDead(byte[] data) {//Used when retrieving data
		super(15);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		
	}
	
	public Packet15SetDead(String usrName) {//Used when sending data from client in the original instance
		super(15);
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
	return (("15" + this.usrName).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getUsrName() {
	return usrName;
}

}
