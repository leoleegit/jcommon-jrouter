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
import java.io.UnsupportedEncodingException;

public class RouterRequest {
	private String data;
	
	protected String method;
	
	protected Object[] parameters;
	
	public RouterRequest(String data){
		init(data);
	}

	public RouterRequest(String method, Object[] parameters){
		this.method = method;
		this.parameters = parameters;
	}
	
	private void init(String data) {
		// TODO Auto-generated method stub
		this.data = data;
		method = RouterUtils.getMethod(data);
		parameters = RouterUtils.getParameters(data);
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}
	
	public String toFlexString() throws UnsupportedEncodingException{
		if(method == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"method\""    + ":\""   + method+"\"");
		for(int i=0; parameters!=null && i<parameters.length; i++)
		{
			sb.append(",");
			Object o = parameters[i];
			if(o==null)o="null";
				sb.append("\"object"+i+"\""    + ":\""   + RouterUtils.encodeToFlex(o) +"\"");				
		}
		sb.append("}");
		return sb.toString();
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
