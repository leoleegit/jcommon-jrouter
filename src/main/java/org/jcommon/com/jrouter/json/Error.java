package org.jcommon.com.jrouter.json;


public class Error extends JsonPacket {
	public static final String ELEMENT = PacketType.ERROR.toString();
	
	private int code;
	private String msg;
	
	public Error(String json){
		super(ELEMENT,json);
	}
	
	public Error(int code, String msg){
		super(ELEMENT,null);
		this.code = code;
		this.msg  = msg;
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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

