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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jcommon.com.jrouter.RouterConnector;
import org.jcommon.com.jrouter.RrouterManager;

import com.glines.socketio.server.SocketIOInbound;
import com.glines.socketio.server.SocketIOServlet;

public class SocketIoServlet extends SocketIOServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RouterConnector _connector;
	
	
	public SocketIoServlet(RouterConnector connector)
	{
		_connector = connector;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		if(_connector==null){
			_connector = new SocketIoConnectorStandalone();
			((SocketIoConnectorStandalone)_connector).startup("localhost", 0);
			RrouterManager.instance().addConnector(_connector);
		}
    }
	
	public SocketIoServlet(){
		
	}
	
	@Override
	protected SocketIOInbound doSocketIOConnect(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return (SocketIOInbound) _connector.addConnection(request);
	}
}
