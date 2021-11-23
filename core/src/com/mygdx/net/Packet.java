package com.mygdx.net;

public abstract class Packet {// An abstract class for all of our packets
	
	public static enum PacketTypes{
		
		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVEMENT(02), SENDHP(03), INITIALSERVERPING(04), 
		SERVERALIVE(05), SENDSTATE(06), ADDITEM(07), REMOVEITEM(8), REQUESTLOBBYINFO(9), 
		SENDLOBBYINFO(10), STARTGAME(11), STOPGAME(12), SENDPLAYERITEM(13), ADDHELDITEM(14),
		SETDEAD(15), SENDGAMEOVER(16), MOVEMENTANTICHEAT(17), USEITEMANTICHEAT(18), USESWORDANTICHEAT(19), 
		NOSWORDANTICHEAT(20), CHANGELEVEL(21), RESETPLAYER(22), LATENCYPING(23), SERVERFULL(24),
		SENDMESSAGE(25), CLIENTALIVE(26), ERRORMESSAGE(27);
		
		private int packetId;
		
		private PacketTypes(int packetId) { //Way to identify packets
			this.packetId = packetId;
		}
		
		public int getId() {
			return packetId;
		}		
	}

	public byte packetId; //packetId stored as a byte
		
	public Packet(int packetId) {//Way to instantiate packet
		this.packetId = (byte) packetId;
	}
		
	public abstract void writeDataToServer(GameClient client); //Sends data from client to the server
	
	public abstract void writeDataToClients(GameServer server); //Sends data from all clients to the server
	
	public abstract byte[] getData(); //The byte array being sent back and forth from the client
	
	/**
	 * Returns the packet id as an int value
	 * @param packetId
	 * @return
	 */
	public static PacketTypes lookupPackets(String packetId) {
		try {
			//System.out.println(PacketTypes.LOGIN);
			return lookupPackets(Integer.parseInt(packetId));
		}catch(NumberFormatException e) {
			//e.printStackTrace();
			//System.out.println(PacketTypes.INVALID);
			return PacketTypes.INVALID;			
		}
	}
	
	protected String readData(byte[] data) {
		String msg = new String(data).trim();
		return msg.substring(2);//The first 2 characters are reserved for the id and are trimmed off
	}
	
	public static PacketTypes lookupPackets(int id) {
		for (PacketTypes p : PacketTypes.values()) {//Loops through all enums in Packets Types eg. login & disconnect
			if(p.getId() == id) { //Returns the packet if it matches the id.
				return p;
			}
		}
		return PacketTypes.INVALID; //Otherwise returns an invalid packet
	}
	
	
}
