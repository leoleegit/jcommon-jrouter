package org.jcommon.com.jrouter.json;


public class Get extends JsonPacket {
	public static final String ELEMENT = PacketType.GET.toString();
	
	public Get(String json){
		super(ELEMENT,json);
	}
	
	@Override
	public boolean isKeepAlive() {
		// TODO Auto-generated method stub
		return false;
	}

}

