package org.jcommon.com.jrouter.test.protel;

import java.io.UnsupportedEncodingException;

import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.jrouter.utils.RouterUtils;

public class RouterRequest implements Packet{
	private String packet_id;
	protected String method;
	
	protected Object[] parameters;
	
	public RouterRequest(String data){
		init(data);
	}
	
	public RouterRequest(String data, String packet_id){
		init(data);
		this.packet_id = packet_id;
	}

	public RouterRequest(String method, Object[] parameters){
		this.method = method;
		this.parameters = parameters;
	}
	
	private void init(String data) {
		// TODO Auto-generated method stub
		method = RouterUtils.getMethod(data);
		parameters = RouterUtils.getParameters(data);
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}
	
	@Override
	public String getPacketID() {
		// TODO Auto-generated method stub
		return packet_id;
	}

	@Override
	public String toPacketString(){
		// TODO Auto-generated method stub
		if(method == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"method\""    + ":\""   + method+"\"");
		for(int i=0; parameters!=null && i<parameters.length; i++)
		{
			sb.append(",");
			Object o = parameters[i];
			if(o==null)o="null";
				try {
					sb.append("\"object"+i+"\""    + ":\""   + RouterUtils.encodeToFlex(o) +"\"");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean isKeepAlive() {
		// TODO Auto-generated method stub
		return "keepLive".equalsIgnoreCase(getMethod());
	}

}
