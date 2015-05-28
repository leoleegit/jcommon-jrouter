
//window.onload = function(){
//	var js = new jsocket("ws://192.168.2.104:8080/jrouter/ws.socket",
//		{
//		onConnected:function(){
//			alert('onConnected');
//		},
//		onClosed : function(reason){
//			alert('onClosed:'+reason);
//		}
//		});
//	js.connect();
//	var packet_faceory = new default_packet_factory();
////	var packet = new default_packet_factory().generate_keepalive_packet();
////	packet.ping.code = 200;
////	packet.ping.isPing = true;
////	packet.ping['中文'] = '中文';
////	packet.ping.array = [{id:'id'},{code:'中文'}];
////	var msg = jsocket.prototype.util.packet_to_msg(packet);
////	alert(msg);
////	var p = jsocket.prototype.util.msg_to_packet(msg);
////	alert(p);
////	var res200 = packet_faceory.msg_to_packet('{"res":{"id":"packet_id","code":200}}');
////	var is_res_200 = packet_faceory.is_res_200(res200);
////	alert(is_res_200);
//}

function jsocket(url,options){
	this.socket_url = url;
	this.keepalive_frequency = 6 * 1000;
	this.keepalive_thread    = null;
	this.status              = jsocket.prototype.CLOSED;
	this.socket_event        = new jsocket_event(this);
	this.msg_timeout         = 3 * 1000;
	this.msg_retry_times     = 3;
	this.msg_retry_counter   = 0;
	this.msg_cache           = new Array();
	this.msg_retry_thread    = null;
	this.packet_factory      = new default_packet_factory();
	
	this.connection  = null;
	this.onConnected = null;
	this.onClosed    = null;
	this.onMessage   = null;
	
	this.config(options);
}

// Socket state
jsocket.prototype.CONNECTING = 0;
jsocket.prototype.CONNECTED  = 1;
jsocket.prototype.CLOSING    = 2;
jsocket.prototype.CLOSED     = 3;

// Disconnect Reason
jsocket.prototype.DR_DISCONNECT = 'DR_DISCONNECT'; //close by local
jsocket.prototype.DR_TIMEOUT    = 'DR_TIMEOUT'; // close due timeout
jsocket.prototype.DR_ERROR      = 'DR_ERROR'; // close due error
jsocket.prototype.DR_CLOSED     = 'DR_CLOSED'; // close by remote
jsocket.prototype.DR_CONNECT_FAIR = 'DR_CONNECT_FAIR'; //close by local


function default_packet_factory(options){
	this.generate_keepalive_packet = function(){
		var packet = {
			ping : {
				packet_id : jsocket.prototype.util.generate_packet_id()
			}
		};
		return packet;
		//{"ping":{"id":"packet_id"}}
		//<ping id="packet_id"></ping>
	}
	this.is_res_200 = function(packet){
		//{"res":{"id":"packet_id","code":200}}
		if(packet && packet['res']){
			return 200 == packet['res']['code'] && 'auto'==packet['res']['type'];
		}
	}
	this.msg_to_packet = function(msg){
		return jsocket.prototype.util.msg_to_packet(msg);
	}
	this.packet_to_msg = function(packet){
		return jsocket.prototype.util.packet_to_msg(packet);
	}

	this.config(options);
}

default_packet_factory.prototype.config = function(options){
	if(options){
        for (var i in options)
        	if (this.hasOwnProperty(i))
                this[i] = options[i];
	}
}

jsocket.prototype.config = function(options){
	if(options){
        for (var i in options)
        	if (this.hasOwnProperty(i))
                this[i] = options[i];
	}
}

jsocket.prototype.keepalive = function(obj){
	this_ = obj;
	if(this_.packet_factory){
		var packet = this_.packet_factory.generate_keepalive_packet();
		this_.send(packet);
	}
}

function jsocket_event(jsocket_obj){
	var this_ = jsocket_obj;
	this.onConnected = function(event){
		this_.status = this_.CONNECTED;
		if(!this_.keepalive_thread){
			this_.keepalive_thread = setInterval(this_.keepalive,this_.keepalive_frequency,this_);
		}
		if(this_.onConnected)
			this_.onConnected(event);
	}
	this.onMessage   = function(m){
		var event = m[0];
		var msg = event.data?event.data:event;
		var packet = msg;
		if(this_.packet_factory){
			packet = this_.packet_factory.msg_to_packet(msg);
			if(this_.packet_factory.is_res_200(packet)){
				if(this_.msg_retry_thread){
					clearTimeout(this_.msg_retry_thread);
					this_.msg_retry_thread = 0;
					this_.msg_retry_counter = 0;
					this_.msg_cache.shift();
					var msg = this_.msg_cache.shift();
					if(msg){
						this_.send(msg);
					}
				}
				return;
			}
		}
		
		if(this_.onMessage)
			this_.onMessage(packet);
	}
	this.onClosed   = function(reason){
		if(this_.disconnect()){
			return;
		}
		var dr = this_.CLOSED;
		if(this_.status == this_.CLOSING){
			dr = this_.DR_DISCONNECT;
		}else if(this_.status == this_.CONNECTING){
			dr = this_.DR_CONNECT_FAIR;
		}else if(reason && reason == this_.DR_TIMEOUT){
			dr = this_.DR_TIMEOUT;
		}else if(reason){
			dr = this_.DR_ERROR;
		}
		
		this_.status = this_.CLOSED;
		if(this_.keepalive_thread){
			clearInterval(this_.keepalive_thread);
			this_.keepalive_thread = 0;
		}
		if(this_.msg_retry_thread){
			clearTimeout(this_.msg_retry_thread);
			this_.msg_retry_thread = 0;
		}
		if(this_.onClosed)
			this_.onClosed(dr);
	}
}

jsocket.prototype.connect = function(url){
	this.status = this.CONNECTING;
	var url_ = url?url:this.socket_url;
	if(!url_){
		throw 'socket url can be null';
		return;
	}
	var event = this.socket_event;
	if('io' in window){
		var host_ = this.util.get_host(url_);
		var port_ = this.util.get_port(url_);
		var resource_ = this.util.get_resource(url_);
		var secure_ = this.util.get_secure(url_);
		this.connection = new io.Socket(host_, {
            port: port_,
            resource: resource_,
            secure: secure_});
        this.connection.connect();

        this.connection.on('connect', function(){
        	event.onConnected(arguments)}
        );
        this.connection.on('disconnect', function(){
        	event.onClosed(arguments)}
        );
        this.connection.on('message', function(){
        	event.onMessage(arguments)}
        );
	}else{
		if (!("WebSocket" in window))
            window.WebSocket = MozWebSocket;
        this.connection = new WebSocket(url_);
        this.connection.onopen = function(){
        	event.onConnected(arguments)};
        this.connection.onmessage = function(){
        	event.onMessage(arguments)};
        this.connection.onclose   = function(){
        	event.onClosed(arguments)};
        this.connection.onerror   = function(){
        	event.onClosed(arguments)};
	}
}

jsocket.prototype.close = function(close_reason){
	if(close_reason){
		this.send(close_reason);
		setTimeout(this.close, 1000);
	}else{
		if(this.connected()){
			this.status = this.CLOSING;
			this.connection.close();
		}
	}
}

jsocket.prototype.msg_retry = function(obj){
	var this_ = obj;
	
	if(this_.msg_retry_counter<this_.msg_retry_times){
		clearTimeout(this_.msg_retry_thread);
		this_.msg_retry_thread = 0;
		this_.msg_retry_counter ++;
		var msg = this_.msg_cache.shift();
		this_.send(msg,1);
	}else{
		this_.socket_event.onClosed(this_.DR_TIMEOUT);
		this_.close();
	}
}

jsocket.prototype.send = function(msg,is_resend){
	if(!msg)
		return;
	if(is_resend)
		this.msg_cache.unshift(msg);
	else
		this.msg_cache.push(msg);
	if(!this.msg_retry_thread){
		this.msg_retry_thread = setTimeout(this.msg_retry,this.msg_timeout,this);
		if(this.connected() && msg){
			if(this.packet_factory){
				msg = this.packet_factory.packet_to_msg(msg);
			}
			this.connection.send(msg);
		}	
	}
}

jsocket.prototype.connected = function(){
	return this.status == this.CONNECTED;
}

jsocket.prototype.disconnect = function(){
	return this.status == this.CLOSED;
}

jsocket.prototype.util = {
	get_host : function(url){
		var str = new String(url);
		if(str.startWith("wss://"))
			str = str.substring(6);
		if(str.startWith("ws://")){
			str = str.substring(5);
		}	
		if(str.indexOf('/')!=-1)
			str = str.substring(0,str.indexOf('/'));
		if(str.indexOf(':')!=-1)
			str = str.split(':')[0];
		return str;
	},
	get_port : function(url){
		var str = new String(url);
		var port = 80;
		if(str.startWith("wss://")){
			str = str.substring(6);
			port = 443;
		}
		if(str.startWith("ws://")){
			str = str.substring(5);
		}
		if(str.indexOf('/')!=-1)
			str = str.substring(0,str.indexOf('/'));
		if(str.indexOf(':')!=-1)
			port = parseInt(str.split(':')[1]);
		return port;
	},
	get_resource : function(url){
		var str = new String(url);
		if(str.startWith("wss://"))
			str = str.substring(6);
		if(str.startWith("ws://")){
			str = str.substring(5);
		}	
		if(str.indexOf('/')!=-1)
			str = str.substring(str.indexOf('/')+1);
		return str;
	},
	get_secure : function(url){
		var str = new String(url);
		if(str.startWith("wss://"))
			return true;
		else
			return false;
	},
	generate_packet_id : function(){
		return this.generate_mixed(15);
	},
	generate_mixed : function(n){
		 var chars = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
		 var res = "";
	     for(var i = 0; i < n ; i ++) {
	         var id = Math.ceil(Math.random()*35);
	         res += chars[id];
	     }
	     return res;
	},
	packet_to_msg : function(packet){
		var msg = '';
		if(packet && this.is_option_object(packet)){
			msg = msg + '{';
			for(var key in packet){
				var value = packet[key];
				var is_option = this.is_option_object(value);
				if(is_option || (value instanceof Array))
					value = this.packet_to_msg(value);
				else if(typeof value === 'number' || typeof value === 'boolean')
					value = value;
				else
					value = '"'+ encodeURIComponent(value) + '"';
				key = '"'+ encodeURIComponent(key) + '"';
				msg = msg + key + ':' + value + ','
			}
			if(msg.lastWith(","))
				msg = msg.substring(0,msg.length-1);
			msg = msg + '}';
		}else if(packet && (typeof packet === "object") && (packet instanceof Array)){
			if(this.has_prop(packet)){
				msg = msg + '[';
				for(var key in packet){
					if(this.is_option_object(packet[key])){
						msg = msg + this.packet_to_msg(packet[key])+ ',';
					}
				}
				if(msg.lastWith(","))
					msg = msg.substring(0,msg.length-1);
				msg = msg + ']';
			}
		}else{
			msg = packet;
		}
		return msg;
	},
	has_prop : function(obj){
		var hasProp = false;  
	    for (var prop in obj){ 
	    	if(this.is_option_object(obj[prop])){
		    	hasProp = true;  
		        break; 
	    	} 
	    }  
	    return hasProp;
	},
	is_option_object : function(obj){
		if(obj && (typeof obj === "object") && !(obj instanceof Array)){
			var hasProp = false;  
		    for (var prop in obj){  
		        hasProp = true;  
		        break;  
		    }  
		    return hasProp;
		}
		return false;
	},
	msg_to_packet : function(msg){
		var packet = new Object();
		if(msg){
			if(msg.startWith('{'))
				packet = eval('('+msg+')');
			else if(msg.startWith('['))
				packet = eval(msg);
		}
		return this.decode_packet(packet);
	},
	decode_packet : function(packet){
		if(packet && this.is_option_object(packet)){
			for(var key in packet){
				var value      = packet[key];
				var decode_key = decodeURIComponent(key);
				
				var is_option = this.is_option_object(value);
				if(is_option || (value instanceof Array))
					value = this.decode_packet(value);
				else
				    value = decodeURIComponent(value);
				
				if(key != decode_key)
					delete packet[key];
				
				packet[decode_key] = value;
			}
		}
		else if(packet && (typeof packet === "object") && (packet instanceof Array)){
			var new_packet = new Array();
			for(var key in packet){
				var opts;
				if(this.is_option_object(packet[key])){
					opts = this.decode_packet(packet[key]);
				}else
					opts = decodeURIComponent(packet[key]);
				new_packet.push(opts);
			}
			return new_packet;
		}
		return packet;
	},
	find_message_event : function(arg){
		var name = 'MessageEvent';
		var event = null;
		if(arg){
			for(var key in arg){
				if(key == name)
					return arg[key];
				if((typeof arg[key] === name))
					return arg[key];
				event = this.find_message_event(arg[key]);
				if(event)
					return event;
			}
		}
		return event;
	}
}

//string
String.prototype.startWith = function(str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substr(0, str.length) == str)
        return true;
    else
        return false;
};
String.prototype.lastWith = function(str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substr(this.length - str.length, this.length) == str)
        return true;
    else
        return false;
};
String.prototype.trim = function(str) {
    if (!str) str = " ";
    var temp = this;
    while (temp.startWith(str))
        temp = temp.substring(1);
    while (temp.lastWith(str))
        temp = temp.substring(0, temp.length - 1);
    return temp;
};
String.prototype.replaceAll = function(str1, str2) {
    var temp = this;
    while (temp.indexOf(str1) != -1)
        temp = temp.replace(str1, str2);
    return temp;
};





