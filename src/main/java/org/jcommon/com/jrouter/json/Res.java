package org.jcommon.com.jrouter.json;

import org.jcommon.com.util.JsonObject;

public class Res extends JsonPacket {
	public static final String ELEMENT = PacketType.RES.toString();
	
	private int code;
	private JsonObject res;
	private String type;
	
	public Res(String json) {
		super(ELEMENT,json);
		// TODO Auto-generated constructor stub
	}
	
	public Res(JsonPacket father){
		this(null,null,0);
		if(father!=null){
			super.setPacket_id(father.getPacket_id());
			setType(father.getElement_name());
		}
	}
	
	public Res(String id, int code){
		this(null,id,code);
	}
	
	public Res(String element_name, String id, int code){
		super(ELEMENT,null);
		this.code = code;
		this.setPacket_id(id);
		setType(element_name);
	}
	
	@Override
	public boolean isKeepAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public JsonObject getRes() {
		return res;
	}

	public void setRes(JsonPacket res) {
		this.res = res;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
