package org.jcommon.com.jrouter.noio;


import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.jcommon.com.jrouter.RouterRequest;


public class LivechatEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		if (message instanceof RouterRequest) {
			RouterRequest request = (RouterRequest) message;
			IoBuffer ioBuffer = IoBuffer.wrap(ByteBuffer.wrap(request.toFlexString().getBytes("utf-8")));
			ioBuffer.position(ioBuffer.capacity());
			ioBuffer.flip();
			out.write(ioBuffer);
		}
	}
}