package org.jcommon.com.jrouter.json;

import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.util.JsonObject;
import org.jcommon.com.util.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonPacket extends JsonObject implements Packet{
	private String packet_id;
	private String element_name_;
	
	public JsonPacket(){}
	
	public JsonPacket(String element_name, String json){
		this.element_name_ = element_name;
		String name = getElement_name();
		JSONObject jsonO = JsonUtils.getJSONObject(json);
		if(jsonO!=null && jsonO.has(name)){
			try {
				json = jsonO.getString(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.error("", e);
			}
		}
		super.setJson(json);
		super.setDecode(true);
		json2Object(this, this.getJson());
	}
	
	public JsonPacket(String json){
		this(null,json);
	}
	
	@Override
	public String getPacketID() {
		// TODO Auto-generated method stub
		return packet_id;
	}
	
	//{name:{}}
	public String toJson(){
		String name = element_name_==null?this.getClass().getSimpleName().toLowerCase():element_name_;
		return toJson(name);
	}
	
	public String toJson(String element_name){
		if(element_name==null)
			return super.toJson();
		String str = super.toJson();
		return "{"+element_name+":"+str+"}";
	}
	
	public boolean has(String key){
		JSONObject jsonO = org.jcommon.com.util.JsonUtils.getJSONObject(this.getJson());
		if(jsonO!=null){
			return jsonO.has(key);
		}
		return false;
	}
	
	public String toPacketString(){
		return toJson();
	}
	
	public void setElement_name(String element_name) {
		this.element_name_ = element_name;
	}
	
	public String getElement_name() {
		return element_name_==null?this.getClass().getSimpleName().toLowerCase():element_name_;
	}

	public Res generateRes() {
		return new Res(this);
	}

	public String getPacket_id() {
		return packet_id;
	}

	public void setPacket_id(String packet_id) {
		this.packet_id = packet_id;
	}
}
