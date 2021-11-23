package com.mygdx.net;

public class Packet01Disconnect extends Packet{//The packet sent at disconnect

	private String usrName;
	
	public Packet01Disconnect(byte[] data) {//Used when retrieving data
		super(01);
		this.usrName = readData(data);
	}
	
	public Packet01Disconnect(String usrName) {//Used when sending data from client in the original instance
		super(01);
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
	return (("01" + this.usrName).getBytes());
}

public String getName() {
	return usrName;
}
	
	
	

}
