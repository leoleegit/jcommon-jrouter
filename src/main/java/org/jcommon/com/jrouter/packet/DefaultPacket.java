package org.jcommon.com.jrouter.packet;

public class DefaultPacket implements Packet{
	private String packet_id;
	private int res_code;
	private String packet_data;
	
	public DefaultPacket(String packet_data){
		this.packet_data = packet_data;
	}
	
	public DefaultPacket(String packet_id,String packet_data){
		this.packet_id   = packet_id;
		this.packet_data = packet_data;
	}
	
	public DefaultPacket(String packet_id,int res_code){
		this.packet_id   = packet_id;
		this.setRes_code(res_code);
	}
	
	@Override
	public String getPacketID() {
		// TODO Auto-generated method stub
		return packet_id;
	}

	@Override
	public boolean isKeepAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPacket_data() {
		return packet_data;
	}

	public void setPacket_data(String packet_data) {
		this.packet_data = packet_data;
	}

	public String getPacket_id() {
		return packet_id;
	}

	public void setPacket_id(String packet_id) {
		this.packet_id = packet_id;
	}

	public int getRes_code() {
		return res_code;
	}

	public void setRes_code(int res_code) {
		this.res_code = res_code;
	}

}
