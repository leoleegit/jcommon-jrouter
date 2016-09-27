package org.jcommon.com.jrouter;

public class SocketKeepAliveFactory {
	
	public SocketKeepAlive createSocketKeepAlive(RouterConnection _connection){
	    return new SocketKeepAlive(_connection);
	}
}
