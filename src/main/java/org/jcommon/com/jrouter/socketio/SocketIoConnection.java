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
package org.jcommon.com.jrouter.socketio;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.jcommon.com.jrouter.AbstractRouterConnection;
import org.jcommon.com.jrouter.RouterConnector;
import org.jcommon.com.jrouter.RrouterManager;
import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.jrouter.utils.ConnectionTask;
import org.jcommon.com.jrouter.utils.DisConnectReason;
import org.jcommon.com.jrouter.utils.RouterUtils;
import org.jcommon.com.jrouter.utils.SocketState;

import com.glines.socketio.common.ConnectionState;
import com.glines.socketio.common.DisconnectReason;
import com.glines.socketio.server.SocketIOInbound;
import com.glines.socketio.server.SocketIOOutbound;

public class SocketIoConnection extends AbstractRouterConnection  implements SocketIOInbound{
	
	protected SocketIOOutbound _connection;
	
	public SocketIoConnection(RouterConnector connector, HttpServletRequest request) {
		super(connector);
		// TODO Auto-generated constructor stub
		try
		{
			_localAddr = InetAddress.getByName(request.getLocalAddr());
			_localPort = request.getLocalPort();
			_remoteAddr = InetAddress.getByName(request.getRemoteAddr());
			_remotePort = request.getRemotePort();
			super.setAttribute(RESOURCE, request.getPathInfo());
		}
		catch (Exception e) 
		{
			LOG.warn(e);
		}
	}

	@Override
	public void doClose(DisConnectReason reason,  String string) {	
		// TODO Auto-generated method stub
		if(isDisconnected() || isClosing())
			return;
		LOG.info(RouterUtils.key(_remoteAddr,_remotePort)+"-->"+reason+ ": "+string);
		setState(SocketState.CLOSING);
		if(_connection!=null){
			_connection.disconnect();
			_connection = null;
		}
		super.onClose(reason, string);
	}

	@Override
	public void process(Packet packet) throws IOException {
		// TODO Auto-generated method stub
		if(_connection != null && packet!=null){
			String msg = packet.toPacketString();
			if(msg!=null)
				_connection.sendMessage(msg);
		}
	}

	@Override
	public void onConnect(SocketIOOutbound arg0) {
		// TODO Auto-generated method stub
		_connection = arg0;
		setState(SocketState.CONNECTED);
		RrouterManager.pool.execute(new RouterTask(ConnectionTask.onOpen));
	}

	@Override
	public void onDisconnect(DisconnectReason arg0, String arg1) {
		// TODO Auto-generated method stub	
		LOG.info(RouterUtils.key(_remoteAddr,_remotePort)+"-->"+arg0+ ": "+arg1);
		String str      = arg1==null?"close connection":arg1;
		DisConnectReason dr = com.glines.socketio.common.DisconnectReason.ERROR == arg0?
				DisConnectReason.ERROR:DisConnectReason.CLOSED;
		if(!isClosing())
			super.onClose(dr, str);
		if(_connection!=null && _connection.getConnectionState()==ConnectionState.CONNECTED){
			_connection.disconnect();
			_connection = null;
		}
	}

	@Override
	public void onMessage(int arg0, String arg1) {
		// TODO Auto-generated method stub
		onRouterStr(arg1);
	}
}
