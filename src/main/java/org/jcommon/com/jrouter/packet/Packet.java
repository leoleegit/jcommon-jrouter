package org.jcommon.com.jrouter.packet;

public interface Packet {
	public String  getPacketID();
	public String  toPacketString();
	public boolean isKeepAlive();
}
