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
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class RouterUtils {
	private static final Logger LOG = Logger.getLogger(RouterUtils.class.getName());
	
	public static String key(RouterConnection connection) 
	{
		return key(connection.getRemoteAddress(), connection.getRemotePort());
	}
	
	public static String key(InetAddress addr, int port) 
	{
		if(addr!=null)
			return addr.getHostAddress() + ":" + port;
		else
			return "127.0.0.1" + ":" + port;
	}
	
	public static String getIp(String key){
		if(key==null)return null;
		if(key.indexOf(":")!=-1)
			return key.split(":")[0];
		return null;
	}
	
	private final static String method = "method";
	private final static String streamID = "streamID";
	private final static String groupID = "groupID";
	

	public static String getGroupID(String data) {
		// TODO Auto-generated method stub
		return getRequestValue(data,groupID);
	}
	
	public static String getMethod(String data) {
		// TODO Auto-generated method stub
		return getRequestValue(data,method);
	}
	

	public static String getStreamID(String data) {
		// TODO Auto-generated method stub
		return getRequestValue(data,streamID);
	}
	
	public static String getRequestValue(String json,String key){
		try{			
			 if(json.indexOf("{")!=-1)
				 json = json.substring(1);
			 if(json.indexOf("}")!=-1)
				 json = json.substring(0,json.indexOf("}"));
			 if(json.indexOf(",")!=-1){
				String[] args = json.split(",");
				//int j = 0;
				for(int i=0;i<args.length;i++){
					String _key   = getKey(args[i].trim());
					String _value = getValue(args[i].trim(),_key);
					if(key.equals(_key)){
						return _value;
					}
					//else j++;
				}	
			}	
		}catch(UnsupportedEncodingException e){
			LOG.warn(e);
		}
		return null;
	}
	
	public static Object[] getParameters(String json){	
		return getParameters(null, json);
	}
	
	public static Object[] getParameters(String id, String json){
		List<String> list  = new ArrayList<String>();	
		try{			
			 if(json.indexOf("{")!=-1)
				 json = json.substring(1);
			 if(json.indexOf("}")!=-1)
				 json = json.substring(0,json.indexOf("}"));
			 if(json.indexOf(",")!=-1){
				String[] args = json.split(",");
				//int j = 0;
				for(int i=0;i<args.length;i++){
					String _key   = getKey(args[i].trim());
					String _value = getValue(args[i].trim(),_key);
					if(!streamID.equals(_key) && !method.equals(_key)){
						list.add( _value );
						//j++;
					}
				}	
			}	
		}catch(UnsupportedEncodingException e){
			LOG.warn(e.getMessage());
		}
		Object[] parameters = null;
		if(id == null){
			parameters = new Object[list.size()];
			for(int i=0; i<list.size(); i++){
				parameters[i] = list.get(i);
			}
		}else{
			parameters = new Object[list.size()+1];
			parameters[0] = id;
			for(int i=0; i<list.size(); i++){
				parameters[i+1] = list.get(i);
			}
		}
		
		return parameters;
	}
	
	public static String  getKey(String data){
		  if(data.startsWith("\""))
			  data = data.substring(1);
		  if(data.indexOf("\"")!=-1)
			  data = data.substring(0,data.indexOf("\""));
		  return data;
	}
	  
	public static String  getValue(String data, String key) throws UnsupportedEncodingException{
		  data = data.replaceAll(key, "");
		  if(data.indexOf(":")!=-1)
			  data = data.substring(data.indexOf(":")+1);
		  if(data.startsWith("\""))
			  data = data.substring(1);
		  if(data.lastIndexOf("\"")!=-1)
			  data = data.substring(0,data.lastIndexOf("\""));
		  return URLDecoder.decode(data, "utf-8");
	}
	
	public static String encodeToFlex(Object o) throws UnsupportedEncodingException{
		String url = java.net.URLEncoder.encode((String)o, "utf-8");
		url        = url.replaceAll("\\+", "%20");
		return url;
	}
}
