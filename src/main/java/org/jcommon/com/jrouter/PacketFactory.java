package org.jcommon.com.jrouter;

import org.jcommon.com.jrouter.packet.Packet;

public interface PacketFactory {
	public Packet generateResPacket(Packet packet,int code);
	public Packet generatePacket(String str);
}
