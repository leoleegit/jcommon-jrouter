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
import org.jcommon.com.jrouter.RrouterManager;
import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.jrouter.utils.ConnectionTask;
import org.jcommon.com.jrouter.utils.DisConnectReason;
import org.jcommon.com.jrouter.utils.RouterUtils;
import org.jcommon.com.jrouter.utils.SocketState;
import org.jcommon.com.jrouter.websocket.jetty9.BufferUtil;
import org.jcommon.com.jrouter.websocket.jetty9.OpCode;
import org.jcommon.com.jrouter.websocket.jetty9.WebSocketFrame;
public class WebSocketConnection extends AbstractRouterConnection implements WebSocket.OnTextMessage, WebSocket.OnFrame, WebSocket.OnControl{

	protected Connection _connection;
	protected FrameConnection _frame_connection;
	
	public WebSocketConnection(RouterConnector connector, HttpServletRequest request) {
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
	public void doClose(DisConnectReason reason, String string) {
		// TODO Auto-generated method stub
		if(isDisconnected() || isClosing())
			return;
		LOG.info(RouterUtils.key(_remoteAddr,_remotePort)+"-->"+reason+ ": "+string);
		setState(SocketState.CLOSING);
		super.onClose(reason, string);
		if(_connection!=null){
			_connection.disconnect();
			_connection = null;
		}
	}

	@Override
	public void process(Packet packet) throws IOException {
		// TODO Auto-generated method stub
		if(_connection != null && packet!=null){
			//LOG.info(packet);
			if(packet.isKeepAlive() && _frame_connection!=null){
				WebSocketFrame frame = WebSocketFrame.ping().setPayload("hello");
				_frame_connection.sendControl(frame.getOpCode(), BufferUtil.toArray(frame.getPayload()), frame.getPayloadStart(), frame.getPayloadLength());
			}else{
				String msg = packet.toPacketString();
				if(msg!=null)
					_connection.sendMessage(msg);
			}
		}
	}

	@Override
	public void onClose(int arg0, String arg1) {
		// TODO Auto-generated method stub
		LOG.info(RouterUtils.key(_remoteAddr,_remotePort)+"-->"+getDisConnectReason(arg0)+ ": "+arg1);
		String str      = arg1==null?"close connection":arg1;
		if(!isClosing())
			super.onClose(DisConnectReason.CLOSED, str);
		setState(SocketState.CLOSED);
		if(_connection!=null){
			_connection.disconnect();
			_connection = null;
		}
	}
	
    final static int CLOSE_NORMAL  =1000;
    final static int CLOSE_SHUTDOWN=1001;
    final static int CLOSE_PROTOCOL=1002;
    final static int CLOSE_BADDATA =1003;
    final static int CLOSE_LARGE   =1004;
	private String getDisConnectReason(int arg0){
		switch(arg0){
			case CLOSE_NORMAL:return   "CLOSE_NORMAL";
			case CLOSE_SHUTDOWN:return "CLOSE_SHUTDOWN";
			case CLOSE_PROTOCOL:return "CLOSE_PROTOCOL";
			case CLOSE_BADDATA:return  "CLOSE_BADDATA";
			case CLOSE_LARGE:return    "CLOSE_LARGE";
		}
		return "UNKNOW";
	}

	@Override
	public void onOpen(Connection arg0) {
		// TODO Auto-generated method stub
		_connection = arg0;
		setState(SocketState.CONNECTED);
		RrouterManager.pool.execute(new RouterTask(ConnectionTask.onOpen));
	}

	@Override
	public boolean onControl(byte controlCode, byte[] data, int offset, int length) {
		// TODO Auto-generated method stub
		try{
			if(OpCode.PING == controlCode){
				//LOG.info("KeepAlive-"+OpCode.name(controlCode));
				WebSocketFrame frame = WebSocketFrame.pong().setPayload("hello");
				_frame_connection.sendControl(frame.getOpCode(), BufferUtil.toArray(frame.getPayload()), frame.getPayloadStart(), frame.getPayloadLength());
			
			}
		}catch(IOException e){
			LOG.error("", e);
		}
		
		if(getSocketKeepAlive()!=null)
			getSocketKeepAlive().updateAliveTime();
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
		_frame_connection = arg0;
	}

	@Override
	public void onMessage(String arg0) {
		// TODO Auto-generated method stub
		onRouterStr(arg0);
	}

}
