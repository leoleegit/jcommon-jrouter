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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcommon.com.jrouter.utils.RouterUtils;

public abstract class AbstractRouterConnector implements RouterConnector{
	
	public static final int WS_DEFAULT_PORT = 80;
	public static final int WSS_DEFAULT_PORT = 443;
	
	public static final String version = "v1.0_20140911";
	public static final String project = "jcommon jrouter";
	
	protected InetAddress _localAddr;
	protected int _localPort;
	protected Map<String, RouterConnection> _connections = new HashMap<String, RouterConnection>();
	
    private List<RouterConnectorListener> listeners = new ArrayList<RouterConnectorListener>();
    
    @Override
	public void removeConnection(RouterConnection connection) {
		// TODO Auto-generated method stub
		synchronized (_connections)
		{
			if(_connections.containsKey(RouterUtils.key(connection)))
				_connections.remove(RouterUtils.key(connection));
		}
	}
	
	public void addConnectorListener(RouterConnectorListener arg0){
		synchronized (listeners)
		{
			listeners.add(arg0);
		}
	}
	
	public void removeConnectorListener(RouterConnectorListener arg0){
		synchronized (listeners)
		{
			listeners.remove(arg0);
		}
	}
	
	public void onConnectorChange(RouterConnection connection){
		synchronized (listeners){
			for(RouterConnectorListener l : listeners){
				if(connection==null)
					l.onStop();
				else{
					l.onOpen(connection);
				}
			}
		}
	}

	@Override
	public RouterConnection addConnection(RouterConnection connection) {
		// TODO Auto-generated method stub
		synchronized (_connections){
			if(!_connections.containsKey(RouterUtils.key(connection))){
				_connections.put(RouterUtils.key(connection), connection);
				onConnectorChange(connection);
			}
		}
		
		return connection;
	}

	@Override
	public InetAddress getAddr() {
		// TODO Auto-generated method stub
		return _localAddr;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return _localPort;
	}
}
