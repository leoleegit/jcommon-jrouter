package org.jcommon.com.jrouter.test.protel;

import org.jcommon.com.jrouter.PacketFactory;
import org.jcommon.com.jrouter.packet.Packet;

public class RouterRequestFactory  implements PacketFactory{

	@Override
	public Packet generateResPacket(Packet packet, int code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Packet generatePacket(String str) {
		// TODO Auto-generated method stub
		return new RouterRequest(str);
	}

}
