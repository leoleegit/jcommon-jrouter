package org.jcommon.com.jrouter.noio;

import org.apache.mina.core.session.IoSession;

public interface ConnectListener {
	public void connectAuth(IoSession session, String connect_group_id, String password);
	
	public void connectOpen(IoSession session, String connect_group_id);
	
	public void connectKeepAlive(IoSession session, String connect_group_id);
	
	public void connectClose(IoSession session, String connect_group_id);
	
	public void sessionOpen(IoSession session, String stream_id);
	
	public void sessionClose(IoSession session, String stream_id, String errorMessage);
	
	public void sessionRouter(IoSession session, String stream_id, String request);
}
