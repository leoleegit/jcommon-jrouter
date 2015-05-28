package org.jcommon.com.jrouter.json;

public class Ping extends JsonPacket{
	public static final String ELEMENT = PacketType.PING.toString();
	
	public Ping(String json) {
		super(ELEMENT,json);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isKeepAlive() {
		// TODO Auto-generated method stub
		return true;
	}

}
