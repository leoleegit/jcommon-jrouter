package org.jcommon.com.jrouter.packet;

import org.jcommon.com.jrouter.PacketFactory;

public class DefaultPacketFactory implements PacketFactory{

	@Override
	public Packet generateResPacket(Packet packet, int code) {
		// TODO Auto-generated method stub
		return new DefaultPacket(packet!=null?packet.getPacketID():null,code);
	}

	@Override
	public Packet generatePacket(String str) {
		// TODO Auto-generated method stub
		return new DefaultPacket(str);
	}

}
