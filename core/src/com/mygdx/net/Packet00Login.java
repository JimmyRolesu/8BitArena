package com.mygdx.net;

public class Packet00Login extends Packet{//The packet sent at login

	private String usrName;
	private float x,y;
	
	public Packet00Login(byte[] data) {//Used when retrieving data
		super(00);
		String[]splitData = readData(data).split(",");
		this.usrName = splitData[0];
		this.x = Float.parseFloat(splitData[1]);
		this.y = Float.parseFloat(splitData[2]);
	}
	
	public Packet00Login(String usrName, float x, float y) {//Used when sending data from client in the original instance
		super(00);
		this.usrName = usrName;
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
	return (("00" + this.usrName+","+this.x+","+this.y).getBytes());
}

public String getName() {
	return usrName;
}

public float getX() {
	return x;
}

public float getY() {
	return y;
}
	
	
	

}
