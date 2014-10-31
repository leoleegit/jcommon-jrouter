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

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket.FrameConnection;
import org.jcommon.com.jrouter.RouterConnection;
import org.jcommon.com.jrouter.RouterRequest;
import org.jcommon.com.jrouter.RouterUtils;
/**
 * @author leoLee
 * 
 */
public class WebSocketKeepAlive implements Runnable {
	private static final Logger LOG = Logger.getLogger(WebSocketKeepAlive.class);
	private FrameConnection _keepalive;
	
	private RouterConnection _connection;
	
	private byte   controlCode; 
	private byte[] data; 
	private int    offset; 
	private int    length;
	
	private boolean run = false;
	
	public WebSocketKeepAlive(FrameConnection _keepalive, RouterConnection _connection){
		this._keepalive  = _keepalive;
		this._connection = _connection;
	}
	
	public void initData(byte controlCode, byte[] data, int offset, int length){
		byte maskLow = 0x0f; 
		if(controlCode != maskLow && data!=null){
			this.controlCode = controlCode;
			this.data        = data;
			this.offset      = offset;
			this.length      = length;
		}else{
			_keepalive = null;
		}
		updateFrequency(frequency);
	}
	
	private long max_idle_time = 26000;
	private long alive_time;
	private long frequency = RouterServer.getFrequency();
	private boolean updated = false;
	private int error_times;
	
	public void updateFrequency(long frequency){	
		this.frequency = frequency;
		this.max_idle_time = frequency + 6000;
		updated = true;
		updateAliveTime();
	}
	
	public void updateAliveTime(){
		this.alive_time = System.currentTimeMillis();
		error_times = 0;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOG.info(RouterUtils.key(_connection)+ " keepalive thread running....");
		int i = 0;
		keeplive();
		while(run){	
			synchronized (this){		
				try {
					Thread.sleep(5000);
					if(!run)break;
					if(i>=frequency || updated){
						keeplive();
						updated = false;
						i = 0;
					}else{
						i = i+ 5000;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LOG.debug("",e);
				}
			}
		}
		LOG.info(RouterUtils.key(_connection)+ " keepalive thread down....");
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean isRun() {
		return run;
	}
	
	private void keeplive(){
		boolean hasException = false;
		if(_keepalive!=null){
			try {
				_keepalive.sendControl(controlCode, data, offset, length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				hasException = true;
			}
		}else{
			try {
				_connection.process(new RouterRequest( "keepLive", new Object[] { "H&S" }));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				hasException = true;
			}
		}

		long now = System.currentTimeMillis();
		if(((now - alive_time) > max_idle_time ) || hasException){
			LOG.info(RouterUtils.key(_connection)+ " keepalive try connect client :"+(error_times+1));
			if(error_times>2){
				_connection.doClose(0,"keeplive connect client fail");
			}else{
				error_times++;
				try {
					java.util.concurrent.TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					LOG.debug("",e1);
				}
				keeplive();
			}
		}
	}

}
              