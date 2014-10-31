package org.jcommon.com.jrouter.noio;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class LivechatCodecFactory implements ProtocolCodecFactory{
	private LivechatEncoder encoder;
	private LivechatDecoder decoder;
	
	public LivechatCodecFactory(){
		encoder = new LivechatEncoder();
		decoder = new LivechatDecoder();
	}
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return decoder;
	}

}
