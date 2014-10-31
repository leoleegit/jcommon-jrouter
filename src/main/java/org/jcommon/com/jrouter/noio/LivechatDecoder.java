package org.jcommon.com.jrouter.noio;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.jcommon.com.jrouter.RouterRequest;

public class LivechatDecoder implements ProtocolDecoder {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		// TODO Auto-generated method stub
		
		byte[] dataBytes = in.array();
		String message = new String(dataBytes, "utf-8");
		
		if(message!=null && "".equals(message))
    		return;
    	//logger.info(message);
    	RouterRequest request = new RouterRequest(message);
    	if(request.getMethod()==null){
    		logger.warn("request format error:"+request.getData());
    	}else{
    		out.write(request);
    	}
    	in.position(in.limit());	
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
