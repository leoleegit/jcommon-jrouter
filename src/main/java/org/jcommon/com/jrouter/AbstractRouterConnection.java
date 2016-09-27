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
package org.jcommon.com.jrouter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.jrouter.utils.ConnectionTask;
import org.jcommon.com.jrouter.utils.DisConnectReason;
import org.jcommon.com.jrouter.utils.RouterUtils;
import org.jcommon.com.jrouter.utils.SocketState;

public abstract class AbstractRouterConnection implements RouterConnection {
	protected static final Logger LOG = Logger.getLogger(AbstractRouterConnection.class);
	protected static final String RESOURCE = "resource";
	private String connection_id;
	private LinkedList<String> packet_cache = new LinkedList<String>(){
		private static final long serialVersionUID = 1L;
		
		private int fix_size = 10;
		
		public boolean add(String e){
			if(super.size()>fix_size){
				super.remove(0);
			}
			return super.add(e);
		}
	};
	protected List<RouterConnectionListener> listeners = new ArrayList<RouterConnectionListener>();
	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	
	protected RouterConnector _connector;
	protected InetAddress _localAddr;
	protected int _localPort;
	protected InetAddress _remoteAddr;
	protected int _remotePort;
	
	protected SocketKeepAlive _keepalive;
	
	protected SocketState state = SocketState.CONNECTED;
	
	public AbstractRouterConnection(RouterConnector connector){
		this._connector = connector;
		if(connector==null)
			return;
		_localPort = _connector.getLocalPort();
		_localAddr = _connector.getAddr();
	}
	
	public SocketState getState() {
		return state;
	}

	public void setState(SocketState state) {
		this.state = state;
	}

	public boolean isConnected(){
		return state == SocketState.CONNECTED;
	}
	
	public boolean isDisconnected(){
		return state == SocketState.CLOSED;
	}
	
	public boolean isClosing(){
		return state == SocketState.CLOSING;
	}
	
	public void onRouterStr(String str){
		if(RrouterManager.instance().getPacketFactory()!=null){
			Packet packet = RrouterManager.instance().getPacketFactory().generatePacket(str);
			if(packet!=null)
				onRouterPacket(packet);
		}else{
			LOG.error("PacketFactory", new Exception("PacketFactory can be null"));
		}
	}
	
	public void onClose(DisConnectReason reason, String string){
		if(isDisconnected())
			return;
		setState(SocketState.CLOSED);
		if(_connector!=null)
			_connector.removeConnection(this);
		if(_keepalive!=null)
			_keepalive.setRun(false);
		_keepalive = null;
		
		LOG.info(RouterUtils.key(_remoteAddr,_remotePort)+"-->"+reason+ " leave ...");
		RouterTask task = new RouterTask(ConnectionTask.onClose, reason, string);
		RrouterManager.pool.execute(task);
		
		if(packet_cache!=null){
			packet_cache.clear();
		}
		
	}
	
	public void onRouterPacket(Packet packet){
		if(packet==null){
			LOG.warn("packet is null");
			return;
		}
		if(isDisconnected())
			return;
		if(RrouterManager.instance().getPacketFactory()!=null){
			try {
				Packet resp = RrouterManager.instance().getPacketFactory().generateResPacket(packet,200);
				if(resp!=null)
					this.process(resp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error("", e);
			}
		}
		if(packet.isKeepAlive()){
			getSocketKeepAlive().updateAliveTime();
			Packet alive_resp = getSocketKeepAlive().response(packet);
			if(alive_resp!=null)
				try {
					process(alive_resp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOG.error("", e);
				}
			return;
		}
		
		if(packet.getPacketID()!=null){
			if(packet_cache.contains(packet.getPacketID())){
				return;
			}else{
				packet_cache.add(packet.getPacketID());
			}
		}
		RrouterManager.pool.execute(new RouterTask(packet, ConnectionTask.onPacket));
	}
	
	protected SocketKeepAlive getSocketKeepAlive(){
		if(isDisconnected())
			return null;
		if(_keepalive==null){
			_keepalive = RrouterManager.instance().getAlive_factory().createSocketKeepAlive(this);
			_keepalive.setRun(true);
			RrouterManager.pool.execute(_keepalive);
		}
		return _keepalive;
	}
	
	@Override
	public String getRouterConnectionId() {
		// TODO Auto-generated method stub
		return connection_id;
	}

	@Override
	public InetAddress getRemoteAddress() {
		// TODO Auto-generated method stub
		return _remoteAddr;
	}

	public InetAddress getLocalAddress(){
		return _localAddr;
	}
	
	public int getLocalPort(){
		return _localPort;
	}
	
	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return _remotePort;
	}

	public void addConnectionListener(RouterConnectionListener listener){
		synchronized(listeners){
			if(!listeners.contains(listener))
				listeners.add(listener);
		}
	}
	
	public void removeConnectionListener(RouterConnectionListener listener){
		synchronized(listeners){
			if(listeners.contains(listener))
				listeners.remove(listener);
		}
	}
	
	public Object getAttribute(String arg0){
		if(attributes.containsKey(arg0))
			return attributes.get(arg0);
		return null;
	}
	
	public void setAttribute(Object arg0, Object arg1){
		synchronized(attributes){
			attributes.put(arg0, arg1);
		}
	}
	
	public void deleteAttribute(Object key){
		synchronized(attributes){
			if(!attributes.containsKey(key))
				attributes.remove(key);
		}
	}
	
	@Override
	public List<RouterConnectionListener> getRouterConnectionListeners() {
		// TODO Auto-generated method stub
		return listeners;
	}
	
	public class RouterTask implements Runnable
    {
    	private Packet packet;
    	private ConnectionTask task;
    	private DisConnectReason reason;
    	private String str;
    	
    	public RouterTask(ConnectionTask task){
    		this.task       = task;
    	}
    	
    	public RouterTask(Packet packet, ConnectionTask task){
    		this.packet     = packet;
    		this.task       = task;
    	}
    	
    	public RouterTask(ConnectionTask task,DisConnectReason reason, String str){
    		this.task   = task;
    		this.reason = reason;
    		this.str    = str;
    	}
    	
    	public void run()
    	{
    		try 
    		{
    			if(task!=null){
    				List<RouterConnectionListener> ls = null;
    				if(task == ConnectionTask.onClose){
    					ls = new ArrayList<RouterConnectionListener>();
    					ls.addAll(listeners);
    				}else
    					ls = listeners;
    				for(RouterConnectionListener l : ls){
        				switch(task){
        					case onPacket : 
        						l.OnWebsocketPacket(AbstractRouterConnection.this, packet);
        						break;
        					case onOpen : 
        						l.OnWebsocketOpen(AbstractRouterConnection.this);
        						break;
        					case onClose :
        						l.OnWebsocketClose(AbstractRouterConnection.this, reason, str);
        						break;
    					default:
    						break;
        				}
        			}
    			}	
    		}
    		catch (Exception e)
    		{
    			LOG.warn(e);
    		}
    	}
    }
}
