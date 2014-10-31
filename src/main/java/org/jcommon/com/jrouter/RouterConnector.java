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

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

public interface RouterConnector {
	public void addConnectorListener(RouterConnectorListener arg0);
	public void removeConnectorListener(RouterConnectorListener arg0);
	public RouterConnection addConnection(RouterConnection connection);
	public RouterConnection addConnection(HttpServletRequest request);
	public void removeConnection(RouterConnection connection);
	public InetAddress getAddr();
	public int getLocalPort();
}
