package org.jcommon.com.jrouter.websocket;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.jcommon.com.jrouter.AbstractRouterConnector;
import org.jcommon.com.jrouter.RouterConnection;
import org.jcommon.com.jrouter.RouterServer;

public class WebSocketConnector extends AbstractRouterConnector implements LifeCycle{

	private static final Logger LOG = Log.getLogger(WebSocketConnector.class.getName());	
	
	protected Connector _httpConnector;
    
	private static WebSocketConnector instance;
	
	public WebSocketConnector(Connector connector){
		_httpConnector = connector;
		try
		{
			_localAddr =  InetAddress.getByName(_httpConnector.getHost());
		}
		catch (Exception e) 
		{
			LOG.warn(e);
		}
		_connections = new HashMap<String, RouterConnection>();
		instance     = this;
	}
	
	private AbstractLifeCycle life_cycle = new AbstractLifeCycle(){
		@Override
	    protected void doStart() throws Exception 
	    {    		
	        LOG.info(String.format("Started {project:%s, version:%s}", project, version), this);
	    }
		
		@Override
		protected void doStop()throws Exception 
		{
			LOG.info(String.format("Stoped {project:%s, version:%s}", project, version), this);
			
			@SuppressWarnings("unchecked")
			Map<String, RouterConnection> _copy = (HashMap<String, RouterConnection>) ((HashMap<String, RouterConnection>) _connections).clone();
			
			for(RouterConnection conn : _copy.values())conn.doClose(0, "shutdown");
			_connections.clear();
			_copy.clear();
			onConnectorChange(null);
			RouterServer.instance().shutdown();
		}
	};
	
	public static WebSocketConnector instance(){return instance;}
	
	@Override
	public RouterConnection addConnection(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return new WebSocketConnection(this, request);
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		life_cycle.start();
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		life_cycle.stop();
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return life_cycle.isRunning();
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return life_cycle.isStarted();
	}

	@Override
	public boolean isStarting() {
		// TODO Auto-generated method stub
		return life_cycle.isStarting();
	}

	@Override
	public boolean isStopping() {
		// TODO Auto-generated method stub
		return life_cycle.isStopping();
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return life_cycle.isStopped();
	}

	@Override
	public boolean isFailed() {
		// TODO Auto-generated method stub
		return life_cycle.isFailed();
	}

	@Override
	public void addLifeCycleListener(Listener listener) {
		// TODO Auto-generated method stub
		life_cycle.addLifeCycleListener(listener);
	}

	@Override
	public void removeLifeCycleListener(Listener listener) {
		// TODO Auto-generated method stub
		life_cycle.removeLifeCycleListener(listener);
	}
}
