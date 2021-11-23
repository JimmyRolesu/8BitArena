package com.mygdx.net;

public class Packet03SendHp extends Packet{//The packet sent at movement

	private String usrName;
	private int maxHp, hp;
	
	public Packet03SendHp(byte[] data) {//Used when retrieving data
		super(03);
		//Stored as: (Username, Max health, current health)
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.usrName = splitData[0];
		this.maxHp = Integer.parseInt(splitData[1]);
		this.hp = Integer.parseInt(splitData[2]); //X and Y are converted into floats so that they can be properly used.
		
		
	}
	
	public Packet03SendHp(String usrName, int maxHp, int hp) {//Used when sending data from client in the original instance
		super(03);
		this.usrName = usrName;
		this.maxHp = maxHp;
		this.hp = hp;
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
	return (("03" + this.usrName + "," + this.maxHp + "," + this.hp).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getName() {
	return usrName;
}

public int getMaxHp(){
	return maxHp;
}

public int getHp() {
	return hp;
}

}
