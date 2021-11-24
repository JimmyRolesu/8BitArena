package com.mygdx.net;

import com.mygdx.entities.PlayerStates;

public class Packet02Movement extends Packet{//The packet sent at movement

	private String usrName;
	private float x,y; //We can store variables such as player coordinates in packets to move the player across all clients
	private int direction;

	
	public Packet02Movement(byte[] data) {//Used when retrieving data
		super(02);
		//Stored as: (Username, x coordinate, y coordinate)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		this.x = Float.parseFloat(splitData[1]);
		this.y = Float.parseFloat(splitData[2]); //X and Y are converted into floats so that they can be properly used.
		this.direction = Integer.parseInt(splitData[3]); //Ditto with the player direction
		//System.out.println(readData(data));
	}
	
	public Packet02Movement(String usrName, float x, float y, int direction) {//Used when sending data from client in the original instance
		super(02);
		this.usrName = usrName;
		this.x = x;
		this.y = y;
		this.direction = direction;
		
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
	return (("02" + this.usrName + "," + this.x + "," + this.y + "," + this.direction).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

public float getX() {
	return this.x;
}

public float getY() {
	return this.y;
}

public int getDirection() {
	return this.direction;
}
	

}
