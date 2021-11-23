package com.mygdx.net;

public class Packet11StartGame extends Packet {

	public Packet11StartGame(byte[] data) {// Used when retrieving data
		super(11);

	}

	public Packet11StartGame() {// Used when sending data from client in the original instance
		super(11);

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
		return (("11").getBytes());
	}

}
