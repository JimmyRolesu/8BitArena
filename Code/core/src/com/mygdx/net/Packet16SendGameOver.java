package com.mygdx.net;

public class Packet16SendGameOver extends Packet{


	private String winner;
	
	public Packet16SendGameOver(byte[] data) {//Used when retrieving data
		super(16);
		//Stored as: 
		String[] splitData = readData(data).split(",");//If received as data, we must split the received string to get individual values. 
		this.winner = splitData[0];
		
	}
	
	public Packet16SendGameOver(String winner) {//Used when sending data from client in the original instance
		super(16);
		this.winner = winner;
		
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
	return (("16" + this.winner).getBytes()); //Returns all values as a string delimited by ",". Converted to bytes in order to send over socket.
}

public String getWinner() {
	return winner;
}

}
