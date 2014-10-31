package org.jcommon.com.jrouter.noio;

public enum Task {
	sessionOpen ("sessionOpen"),
	sessionClose ("sessionClose"),
	sessionRouter ("sessionRouter"),
	
	connectAuth ("connectAuth"),
	connectKeepAlive ("connectKeepAlive");
	
	private String task;
	
	Task(String task){
		this.task = task;
	}
	
	public String toString(){
		return task;
	}
}
