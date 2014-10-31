package org.jcommon.com.jrouter.socketio;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Connector;
import org.jcommon.com.jrouter.RouterConnection;
import org.jcommon.com.jrouter.websocket.WebSocketConnector;

public class SocketIoConnector extends WebSocketConnector{

	private static SocketIoConnector instance;
	
	public SocketIoConnector(Connector connector){
		super(connector);
		instance     = this;
	}
	
	public static SocketIoConnector instance(){return instance;}
	
	@Override
	public RouterConnection addConnection(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return new SocketIoConnection(this, request);
	}
}
