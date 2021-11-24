package com.mygdx.net;

public class Packet17AntiCheatMove extends Packet{


	private String usrName;
	private int direction;
	private long timeDone;

	
	public Packet17AntiCheatMove(byte[] data) {//Used when retrieving data
		super(17);
		//Stored as: (Username, direction)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		this.direction = Integer.parseInt(splitData[1]); //Ditto with the player direction
		this.timeDone = Long.parseLong(splitData[2]);
		//System.out.println(readData(data));
	}
	
	public Packet17AntiCheatMove(String usrName, int direction, long timeDone) {//Used when sending data from client in the original instance
		super(17);
		this.usrName = usrName;
		this.direction = direction;
		this.timeDone = timeDone;
		
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
	return (("17" + this.usrName + "," + this.direction + "," + this.timeDone).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

public int getDirection() {
	return this.direction;
}

public long getTimeDone() {
	return this.timeDone;
}
	

}
