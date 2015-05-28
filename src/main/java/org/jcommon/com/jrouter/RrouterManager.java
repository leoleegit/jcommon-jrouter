package org.jcommon.com.jrouter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jcommon.com.jrouter.packet.DefaultPacketFactory;

public class RrouterManager {
	private static RrouterManager instance;
	public  static ExecutorService pool = Executors.newCachedThreadPool(); 
	private List<RouterConnector> connectors = new ArrayList<RouterConnector>();
	private PacketFactory facetory;
	
	public static RrouterManager instance(){
		if(instance==null){
			instance = new RrouterManager();
		}
		return instance;
	}
	
	public PacketFactory getPacketFactory(){
		if(facetory==null){
			facetory  = new DefaultPacketFactory();
		}
		return facetory;
	}
	
	public void setPacketFacetory(PacketFactory facetory){
		this.facetory = facetory;
	}
	
	public static void  setThreadPool(ExecutorService pool){
		RrouterManager.pool = pool;
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
}
