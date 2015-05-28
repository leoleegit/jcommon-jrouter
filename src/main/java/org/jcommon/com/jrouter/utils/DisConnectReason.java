package org.jcommon.com.jrouter.utils;

public enum DisConnectReason {
	CLOSE(1),
	KEEPALIVEFAIL(2),
	CLOSED(3),
	ERROR(-1),
	SHUTDOWN(4);
	
	public int code;
	
	DisConnectReason(int code){
		this.code = code;
	}
}
