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

import org.apache.log4j.Logger;
import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.jrouter.utils.DisConnectReason;
import org.jcommon.com.jrouter.utils.RouterUtils;

public class SocketKeepAlive implements Runnable{
	protected static final Logger LOG = Logger.getLogger(SocketKeepAlive.class);
	
	protected long max_idle_time = 26000;
	protected long alive_time;
	
	protected boolean run;
	protected RouterConnection _connection;
	
	public SocketKeepAlive(RouterConnection _connection){
		this._connection = _connection;
		setRun(true);
	}
	
	public void updateAliveTime(){
		this.alive_time = System.currentTimeMillis();
	}
	
	public Packet response(Packet keepalive_packet){
		return null;
	}
	
	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean isRun() {
		return run;
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
					Thread.sleep(5000);
					if(!run)break;
					long now = System.currentTimeMillis();
					if(((now - alive_time) > max_idle_time )){
						LOG.info(RouterUtils.key(_connection)+ " keepalive try connect client");
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
