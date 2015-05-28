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

import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.jcommon.com.jrouter.RouterConnector;
import org.jcommon.com.jrouter.RrouterManager;
/**
 * @author leoLee
 * 
 */
public class WebsocketServlet extends WebSocketServlet{
	public static URL init_file_is =WebSocketServlet.class.getResource("/jrouter-log4j.xml");
	private Logger logger = Logger.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RouterConnector _connector;
	
	public WebsocketServlet(RouterConnector connector)
	{
		_connector = connector;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		if(_connector==null){
			_connector = new WebSocketConnectorStandalone();
			((WebSocketConnectorStandalone)_connector).startup("localhost", 0);
			RrouterManager.instance().addConnector(_connector);
		}	
		super.init(config);
    }
	
	public WebsocketServlet(){
		
	}
	
	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol){
		WebSocket socket = null;
		try{
			socket = (WebSocket) _connector.addConnection(request);
		}catch(Exception e){
			logger.error("", e);
		}
		return socket;
	}
	
	static
	  {
	    if (init_file_is != null)
	      DOMConfigurator.configure(init_file_is);
	  }
}

