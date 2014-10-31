package org.jcommon.com.jrouter.noio;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jcommon.com.jrouter.RouterConnection;
import org.jcommon.com.jrouter.RouterConnectionListener;
import org.jcommon.com.jrouter.RouterConnector;
import org.jcommon.com.jrouter.RouterRequest;
import org.jcommon.com.jrouter.RouterServer;
import org.jcommon.com.jrouter.RouterUtils;
import org.jcommon.com.jrouter.WebSocketKeepAlive;

public class NoIOConnection implements RouterConnection, RouterConnectionListener {
	private Logger logger = Logger.getLogger(this.getClass());
	
	protected RouterConnector _connector;
	
	public static final int GroupConnection = 1;
	public static final int Standalone = 2;
	
	private int type;
	
	protected InetAddress _localAddr;
	protected int _localPort;
	protected InetAddress _remoteAddr;
	protected int _remotePort;
	
    private ServerSurrogate serverSurrogate;
    
	private WebSocketKeepAlive keepAliveThread;
	private String connection_id;
	private String stream_id;
	private String connect_group_id;

	private List<RouterConnectionListener> listeners = new ArrayList<RouterConnectionListener>();
	
	public NoIOConnection(RouterConnector _connector, 
			String connect_group_id, InetAddress _remoteAddr, int _remotePort){
		this._connector = _connector;
		this.serverSurrogate  = ((NoIOConnector1)_connector).getServerSurrogate();
		this._remoteAddr= _remoteAddr;
		this._remotePort= _remotePort;
		
		stream_id = RouterUtils.key(this);
		this.connect_group_id = connect_group_id;
	}
	
	@Override
	public InetAddress getRemoteAddress() {
		// TODO Auto-generated method stub
		return _remoteAddr;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return _remotePort;
	}

	@Override
	public void doClose(int i, String string) {
		// TODO Auto-generated method stub
		if(serverSurrogate!=null)
			serverSurrogate.deliver(connect_group_id,new RouterRequest(Task.sessionClose.toString(),new Object[]{stream_id}));
	}

	@Override
	public void process(RouterRequest request) throws IOException {
		// TODO Auto-generated method stub
		if(serverSurrogate!=null)
			serverSurrogate.deliver(connect_group_id,new RouterRequest(Task.sessionRouter.toString(),new Object[]{stream_id,request.toFlexString()}));
	}

	@Override
	public void addConnectionListener(RouterConnectionListener listener) {
		// TODO Auto-generated method stub
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public void removeConnectionListener(RouterConnectionListener listener) {
		// TODO Auto-generated method stub
		if(listeners.contains(listener))
			listeners.remove(listener);
	}

	public boolean onControl(byte arg0, byte[] arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if(keepAliveThread!=null && !keepAliveThread.isRun()){
			keepAliveThread.initData(arg0, arg1, arg2, arg3);
			keepAliveThread.setRun(true);
			RouterServer.pool.execute(keepAliveThread);
		}
		if(keepAliveThread!=null)
			keepAliveThread.updateAliveTime();
		if(keepAliveThread==null)
			logger.warn("keepAliveThread is null");
		return false;
	}

	private void onConnectionChange(RouterRequest request, String arg0){
		Object[] listeners_  = listeners.toArray();
		if(RouterServer.pool!=null)
			RouterServer.pool.execute(new RouterTask(listeners_, request, arg0));
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
    					l.OnWebsocketReuqest(NoIOConnection.this,request);
    				else if(arg0!=null)
    					l.OnWebsocketClose(NoIOConnection.this,arg0);
    				else
    					l.OnWebsocketOpen(NoIOConnection.this);
    			}
    		}
    		catch (Exception e)
    		{
    			logger.warn(e);
    		}
    	}
    }
	
	@Override
	public void OnWebsocketReuqest(RouterConnection connection,
			RouterRequest request) {
		// TODO Auto-generated method stub
		if("setConnectionId".equalsIgnoreCase(request.getMethod())){
			Object[] args = request.getParameters();
			String temp   = args!=null && args.length>0 ? (String)args[0]:null;
			connection_id = temp!=null?temp:connection_id;
			return;
		}
		if("keepLive".equalsIgnoreCase(request.getMethod()))
			onControl((byte)0x0f,null,0,0);
		onConnectionChange(request,null);
	}

	@Override
	public void OnWebsocketClose(RouterConnection connection, String errorMessage) {
		// TODO Auto-generated method stub
		onConnectionChange(null,errorMessage==null?"close connection":errorMessage);
		_connector.removeConnection(this);
		if(keepAliveThread!=null)
			keepAliveThread.setRun(false);
		keepAliveThread = null;
	}

	@Override
	public void OnWebsocketOpen(RouterConnection connection) {
		// TODO Auto-generated method stub
		if(type==Standalone)
			keepAliveThread = new WebSocketKeepAlive(null, this);
		onConnectionChange(null,null);
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	@Override
	public List<RouterConnectionListener> getRouterConnectionListeners() {
		// TODO Auto-generated method stub
		return listeners;
	}

	@Override
	public String getRouterConnectionId() {
		// TODO Auto-generated method stub
		return connection_id;
	}

	@Override
	public InetAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
