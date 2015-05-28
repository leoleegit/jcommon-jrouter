package org.jcommon.com.jrouter.json;

public enum PacketType {
	PING,
	RES,
	SET,
	GET,
	ERROR,
	AUTO;
	
	public String toString(){
		return super.toString().toLowerCase();
	}
}
