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

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.xml.DOMConfigurator;

public class RouterServer {
	public  static ExecutorService pool = Executors.newCachedThreadPool(); 
	
	private Object server;
	private List<RouterConnector> connectors = new ArrayList<RouterConnector>();
	
	private static RouterServer instance = new RouterServer();
	public  static RouterServer instance(){return instance;}
	
	public static final String SPOTLIGHTHOME = "spotlightHome";
	public static final String HOME = System.getProperty(SPOTLIGHTHOME, System.getProperty("user.dir"));
	
	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	private List<AttributesListener> listeners = new ArrayList<AttributesListener>();
	
	private static long frequency = 20000;
	
	static {
		if(HOME.equals(System.getProperty("user.dir")))
			DOMConfigurator.configure(HOME+File.separator+"log4j.xml");	
		else{
			String file_name = "router_log4j.xml";
			DOMConfigurator.configure(HOME+File.separator+"log4j"+File.separator+file_name);	
		}
	}
	
	public RouterServer(){
		if(instance==null)
			RouterServer.instance = this;
	}
	
	public RouterServer(RouterServer instance){
		RouterServer.instance = instance;
	}
	
	public static void setRouterServer(Object instance){
		RouterServer.instance = (RouterServer) instance;
	}
	
	public void setServer(Object server) {
		this.server = server;
	}
	
	public Object getServer() {
		return server;
	}

	public void addConnector(RouterConnector connector) {
		if(!connectors.contains(connector))
			connectors.add(connector);
	}
	
	public boolean removeConnector(RouterConnector connector) {
		if(connectors.contains(connector))
			return connectors.remove(connector);
		return false;
	}

	public List<RouterConnector> getConnectors() {
		return connectors;
	}
	
	public Object getAttribute(String arg0){
		if(attributes.containsKey(arg0))
			return attributes.get(arg0);
		return null;
	}
	
	public void setAttribute(Object arg0, Object arg1){
		synchronized(attributes){
			attributes.put(arg0, arg1);
		}
		if(arg0!=null){
			synchronized(listeners){
				for(AttributesListener listener : listeners)
					listener.setAttribute(arg0, arg1);
			}
		}
	}
	
	public void setAttribute(String arg0, Object arg1){
		synchronized(attributes){
			attributes.put(arg0, arg1);
		}
		if(arg0!=null){
			synchronized(listeners){
				for(AttributesListener listener : listeners)
					listener.setAttribute(arg0, arg1);
			}
		}
	}
	
	public void deleteAttribute(Object key){
		synchronized(attributes){
			if(!attributes.containsKey(key))
				attributes.remove(key);
		}
		if(key!=null){
			synchronized(listeners){
				for(AttributesListener listener : listeners)
					listener.deleteAttribute(key);
			}
		}
	}
	
	public void shutdown(){
		if(pool!=null)
			pool.shutdown();
		pool = null;
	}

	public static void setFrequency(String frequency) {
		RouterServer.frequency = Long.valueOf(frequency);
	}

	public static long getFrequency() {
		return frequency;
	}

	public void addAttributeListener(AttributesListener listener){
		synchronized(listeners){
			if(!listeners.contains(listener)){
				listeners.add(listener);
			}
		}
		
		synchronized(attributes){
			for(Object attr : attributes.keySet()){
				listener.setAttribute(attr, attributes.get(attr));
			}
		}
	}

	public void removeAttributeListener(AttributesListener listener){
		synchronized(listeners){
			if(listeners.contains(listener))
				listeners.remove(listeners);
		}
	}
}
