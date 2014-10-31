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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractRouterConnection implements RouterConnection {
	protected static final Logger LOG = Logger.getLogger(AbstractRouterConnection.class);
	
	private String connection_id;
	
	private List<RouterConnectionListener> listeners = new ArrayList<RouterConnectionListener>();
	
	protected RouterConnector _connector;
	protected InetAddress _localAddr;
	protected int _localPort;
	protected InetAddress _remoteAddr;
	protected int _remotePort;
	
	protected SocketKeepAlive _keepalive;
	
	public AbstractRouterConnection(RouterConnector connector){
		this._connector = connector;
		
		if(connector==null)
			return;
		_localPort = _connector.getLocalPort();
		_localAddr = _connector.getAddr();
	}
	
	public void onRouterRequest(RouterRequest request){
		if("setConnectionId".equalsIgnoreCase(request.getMethod())){
			Object[] args = request.getParameters();
			String temp   = args!=null && args.length>0 ? (String)args[0]:null;
			connection_id = temp!=null?temp:connection_id;
			_connector.addConnection(this);
			return;
		}
		if("keepLive".equalsIgnoreCase(request.getMethod())){
			if(_keepalive==null){
				_keepalive = new SocketKeepAlive(this);
				_keepalive.setRun(true);
				RouterServer.pool.execute(_keepalive);
			}
			_keepalive.updateAliveTime();
			return;
		}
		onConnectionChange(request, null);
	}
	
	protected void onConnectionChange(RouterRequest request, String arg0){
		Object[] listeners_  = listeners.toArray();
		if(RouterServer.pool!=null)
			RouterServer.pool.execute(new RouterTask(listeners_, request, arg0));
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
	
	@Override
	public List<RouterConnectionListener> getRouterConnectionListeners() {
		// TODO Auto-generated method stub
		return listeners;
	}
	
	class RouterTask implements Runnable
    {
    	private Object[] listeners_;
    	private RouterRequest request;
    	private String arg0;
    	
    	public RouterTask(Object[] listeners_, RouterRequest request, String arg0)
    	{
    		this.listeners_ = listeners_;
    		this.request    = request;
    		this.arg0       = arg0;
    	}
    	
    	public void run()
    	{
    		try 
    		{
    			for(Object o : listeners_){
    				RouterConnectionListener l = (RouterConnectionListener) o;
    				if(request!=null)
    					l.OnWebsocketReuqest(AbstractRouterConnection.this,request);
    				else if(arg0!=null)
    					l.OnWebsocketClose(AbstractRouterConnection.this,arg0);
    				else
    					l.OnWebsocketOpen(AbstractRouterConnection.this);
    			}
    		}
    		catch (Exception e)
    		{
    			LOG.warn(e);
    		}
    	}
    }
}
