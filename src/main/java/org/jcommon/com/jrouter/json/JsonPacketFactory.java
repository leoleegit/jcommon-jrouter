package org.jcommon.com.jrouter.json;

import org.apache.log4j.Logger;
import org.jcommon.com.jrouter.PacketFactory;
import org.jcommon.com.jrouter.packet.Packet;
import org.json.JSONObject;

public class JsonPacketFactory implements PacketFactory{
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public Packet generateResPacket(Packet packet, int code) {
		// TODO Auto-generated method stub
		if(packet!=null && packet instanceof JsonPacket){
			Res res = ((JsonPacket)packet).generateRes();
			res.setCode(code);
			res.setType(PacketType.AUTO.toString());
			return res;
		}
		
		return new Res(packet!=null?packet.getPacketID():null,code);
	}

	@Override
	public Packet generatePacket(String str) {
		// TODO Auto-generated method stub
		Packet packet = null;
		JSONObject jsonO = org.jcommon.com.util.JsonUtils.getJSONObject(str);
		if(jsonO==null){
			logger.warn("exception packet:"+str);
			return packet;
		}
		if(jsonO.has("ping")){
			packet =  new Ping(str);
		}else if(jsonO.has("set")){
			packet =  new Set(str);
		}else if(jsonO.has("get")){
			packet =  new Get(str);
		}else if(jsonO.has("error")){
			packet =  new Error(str);
		}
		return packet;
	}

}
