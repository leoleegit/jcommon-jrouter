package org.jcommon.com.jrouter.test;

import org.jcommon.com.jrouter.RrouterManager;
import org.jcommon.com.jrouter.json.JsonPacketFactory;
import org.jcommon.com.util.system.SystemListener;

public class JsonPacketFactoryTest implements SystemListener {

	@Override
	public boolean isSynchronized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		RrouterManager.instance().setPacketFactory(new JsonPacketFactory());
	}

}
