package com.aurelpaulovic.fiit.ec_dstm.net

import org.{ zeromq => zmq }

class RequestConnection (private val context: zmq.ZMQ.Context, private val addr: String) extends Connection {
	private var connection: zmq.ZMQ.Socket = null
    
    def start {
        if(connection == null) {
		    connection = context.socket(zmq.ZMQ.REQ)
		    connection.connect(addr)
        }
    }
	
	def stop {
	    if(connection != null) {
	        
	        connection.close()
	        connection = null
	    }
	}
	
	def ask(msg: message.Message): Option[message.Message] = {
	    if(connection != null) {
	        connection.send(msg)
	        
	        return Some(connection.recvStr(defaultCharset))
	    }
	    
	    None
	}
	
}