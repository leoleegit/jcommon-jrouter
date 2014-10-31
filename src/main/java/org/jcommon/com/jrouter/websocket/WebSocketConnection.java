// ========================================================================
// Copyright 2012 leolee<workspaceleo@gmail.com>
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//     http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================
package org.jcommon.com.jrouter.websocket;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.jcommon.com.jrouter.AbstractRouterConnection;
import org.jcommon.com.jrouter.RouterConnector;
import org.jcommon.com.jrouter.RouterRequest;
import org.jcommon.com.jrouter.RouterServer;
import org.jcommon.com.jrouter.RouterUtils;
import org.jcommon.com.jrouter.SocketKeepAlive;

public class WebSocketConnection extends AbstractRouterConnection implements WebSocket.OnTextMessage, WebSocket.OnFrame, WebSocket.OnControl{

	protected Connection _connection;
	
	public WebSocketConnection(RouterConnector connector, HttpServletRequest request) {
		super(connector);
		// TODO Auto-generated constructor stub
		try
		{
			_localAddr = InetAddress.getByName(request.getLocalAddr());
			_localPort = request.getLocalPort();
			_remoteAddr = InetAddress.getByName(request.getRemoteAddr());
			_remotePort = request.getRemotePort();
		}
		catch (Exception e) 
		{
			LOG.warn(e);
		}
	}

	@Override
	public void doClose(int i, String string) {
		// TODO Auto-generated method stub
		onClose(i, string);
	}

	@Override
	public void process(RouterRequest request) throws IOException {
		// TODO Auto-generated method stub
		if(_connection != null)
			_connection.sendMessage(request.toFlexString());
	}

	@Override
	public void onClose(int arg0, String arg1) {
		// TODO Auto-generated method stub
		if(_connector!=null)
			_connector.removeConnection(this);
		if(_keepalive!=null)
			_keepalive.setRun(false);
		_keepalive = null;
		
		onConnectionChange(null, arg1==null?"close connection":arg1);
		LOG.info(RouterUtils.key(_remoteAddr,_remotePort)+" leave ...");
		
		if(_connection!=null){
			_connection.disconnect();
			_connection = null;
		}
	}

	@Override
	public void onOpen(Connection arg0) {
		// TODO Auto-generated method stub
		_connection = arg0;
		onConnectionChange(null, null);
	}

	@Override
	public boolean onControl(byte arg0, byte[] arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if(_keepalive!=null && !_keepalive.isRun()){
			_keepalive.setRun(true);
			RouterServer.pool.execute(_keepalive);
		}
		if(_keepalive!=null)
			_keepalive.updateAliveTime();
		if(_keepalive==null)
			LOG.warn("keepAliveThread is null");
		return false;
	}

	@Override
	public boolean onFrame(byte arg0, byte arg1, byte[] arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onHandshake(FrameConnection arg0) {
		// TODO Auto-generated method stub
		if(_keepalive==null)
			_keepalive = new SocketKeepAlive(this);
	}

	@Override
	public void onMessage(String arg0) {
		// TODO Auto-generated method stub
		RouterRequest request = new RouterRequest(arg0);
		onRouterRequest(request);
	}

}
