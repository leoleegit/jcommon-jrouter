package org.jcommon.com.jrouter.json;


public class Set extends JsonPacket {
	public static final String ELEMENT = PacketType.SET.toString();
	
	public Set(String json){
		super(ELEMENT,json);
	}
	
	@Override
	public boolean isKeepAlive() {
		// TODO Auto-generated method stub
		return false;
	}

}