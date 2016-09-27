package org.jcommon.com.jrouter.test;

import org.jcommon.com.jrouter.RrouterManager;
import org.jcommon.com.jrouter.json.JsonPacket;
import org.jcommon.com.jrouter.json.JsonPacketFactory;
import org.jcommon.com.jrouter.json.Ping;
import org.jcommon.com.jrouter.json.Res;
import org.jcommon.com.jrouter.packet.Packet;

public class BaseJsonTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RrouterManager.instance().setPacketFactory(new JsonPacketFactory());
		Packet bj = new Res("rwerwerqwrer",200);
		System.out.println(bj);
		Ping ping = new Ping(null);
		ping.setPacket_id("fsdfasdfadf");
		Res res = (Res) RrouterManager.instance().getPacketFactory().generateResPacket(ping,200);
		res.setRes(ping);
		System.out.println(res);
	}
}
