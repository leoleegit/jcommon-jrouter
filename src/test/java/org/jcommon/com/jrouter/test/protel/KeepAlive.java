package org.jcommon.com.jrouter.test.protel;

import java.io.IOException;

import org.jcommon.com.jrouter.RouterConnection;
import org.jcommon.com.jrouter.SocketKeepAlive;
import org.jcommon.com.jrouter.utils.DisConnectReason;
import org.jcommon.com.jrouter.utils.RouterUtils;

public class KeepAlive extends SocketKeepAlive{

	public KeepAlive(RouterConnection _connection) {
		super(_connection);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOG.info(RouterUtils.key(_connection)+ " keepalive thread running....");
		if(alive_time==0)
			updateAliveTime();
		while(run){	
			synchronized (this){		
				try {
					try {
						_connection.process(new RouterRequest( "keepLive", new Object[] { "H&S" }));
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
					Thread.sleep(6000);
					if(!run)break;
					long now = System.currentTimeMillis();
					if(((now - alive_time) > max_idle_time )){
						LOG.info(RouterUtils.key(_connection)+ " keeplive connect client fail");
						_connection.doClose(DisConnectReason.KEEPALIVEFAIL,"keeplive connect client fail");
						run = false;
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LOG.debug("",e);
				}
			}
		}
		LOG.info(RouterUtils.key(_connection)+ " keepalive thread down....");
	}
}
