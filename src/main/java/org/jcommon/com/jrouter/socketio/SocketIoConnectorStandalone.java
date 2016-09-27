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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jcommon.com.jrouter.AbstractRouterConnector;
import org.jcommon.com.jrouter.RouterConnection;
import org.jcommon.com.jrouter.utils.DisConnectReason;

public class SocketIoConnectorStandalone extends AbstractRouterConnector{
	private static final Logger LOG = Logger.getLogger(SocketIoConnectorStandalone.class.getName());
	
	public static final String version = "v1.0_20140822";
	public static final String project = "SocketIo Connector Standalone";
	
	private static SocketIoConnectorStandalone instance;
    
    public static SocketIoConnectorStandalone instance(){return instance;}
    
    public RouterConnection addConnection(HttpServletRequest request){
    	return super.addConnection(new SocketIoConnection(this, request));
	}

    public void startup(String host, int port){
    	try
		{
			_localAddr =  InetAddress.getByName(host);
		}
		catch (Exception e) 
		{
			LOG.warn(e);
		}
		_connections = new HashMap<String, RouterConnection>();
		instance     = this;
    }
    
    public void shutdown(){
    	LOG.info(String.format("Stoped {project:%s, version:%s}", project, version));
		
		@SuppressWarnings("unchecked")
		Map<String, RouterConnection> _copy = (HashMap<String, RouterConnection>) ((HashMap<String, RouterConnection>) _connections).clone();
		
		for(RouterConnection conn : _copy.values())conn.doClose(DisConnectReason.SHUTDOWN, "shutdown");
		_connections.clear();
		_copy.clear();
		onConnectorChange(null);
    }
}
