package org.jcommon.com.jrouter;


import java.util.*;

public class RouterServer {
	private static RouterServer instance;
	public  static RouterServer instance(){
		if(instance==null)
			instance = new RouterServer();
		return instance;
	}
	
	
	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	private List<AttributesListener> listeners = new ArrayList<AttributesListener>();
	

	
	public RouterServer(){
	}
	
	public RouterServer(RouterServer instance){
		RouterServer.instance = instance;
	}
	
	public static void setRouterServer(Object instance){
		RouterServer.instance = (RouterServer) instance;
		System.out.println(instance);
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
