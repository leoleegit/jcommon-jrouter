package org.jcommon.com.jrouter.test;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;
import org.jcommon.com.jrouter.noio.NoIOConnector1;

public class NoIOConnectorTest {
	 public static URL init_file_is = NoIOConnectorTest.class.getResource("/jrouter-log4j.xml");

	public static void main(String[] args) throws IOException{
		new Thread(new Runnable(){
			public void run(){
				try {
					new NoIOConnector1("192.168.2.104",0).doStart();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		System.in.read();
	}
	
	 static
	  {
	    if (init_file_is != null)
	      DOMConfigurator.configure(init_file_is);
	  }
}
