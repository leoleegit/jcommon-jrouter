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
/**
 * @author leoLee
 * 
 */
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.jcommon.com.jrouter.packet.Packet;
import org.jcommon.com.jrouter.utils.DisConnectReason;
import org.jcommon.com.jrouter.utils.SocketState;

public interface RouterConnection {
	public String getRouterConnectionId();
	public InetAddress getLocalAddress();
	public int getLocalPort();
	public InetAddress getRemoteAddress();
	public int getRemotePort();
	public void doClose(DisConnectReason reason, String string);
	public void process(Packet packet)throws IOException;
	public void addConnectionListener(RouterConnectionListener listener);
	public void removeConnectionListener(RouterConnectionListener listener);
	public List<RouterConnectionListener> getRouterConnectionListeners();
	public SocketState getState();
	public boolean isConnected();
	public boolean isDisconnected();
	public Object getAttribute(String arg0);
	public void setAttribute(Object arg0, Object arg1);
	public void deleteAttribute(Object key);
}
