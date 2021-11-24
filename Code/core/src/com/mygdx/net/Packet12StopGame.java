package com.mygdx.net;

public class Packet12StopGame extends Packet {

	public Packet12StopGame(byte[] data) {// Used when retrieving data
		super(12);

	}

	public Packet12StopGame() {// Used when sending data from client in the original instance
		super(12);

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
		return (("12").getBytes());
	}

}
