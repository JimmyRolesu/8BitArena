package com.mygdx.net;

public class Packet06SendState extends Packet{//The packet sent at movement

	private String usrName;
	private int stateInt;
	
	public Packet06SendState(byte[] data) {//Used when retrieving data
		super(06);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		this.stateInt = Integer.parseInt(splitData[1]);
		
		
	}
	
	public Packet06SendState(String usrName, int stateInt) {//Used when sending data from client in the original instance
		super(06);
		this.usrName = usrName;
		this.stateInt = stateInt;
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
	return (("06" + this.usrName + "," + this.stateInt).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

public int getStateInt(){
	return stateInt;
}

}
