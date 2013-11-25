package com.aurelpaulovic.fiit.ec_dstm.net

import org.{ zeromq => zmq }
import identity.Publisher

class PublisherConnection (protected val context: zmq.ZMQ.Context, protected val addr: String ) extends Connection {
    val identity = Publisher(addr)

	private[this] var connection: zmq.ZMQ.Socket = null
    
    def start {
        if(connection == null) {
		    connection = context.socket(zmq.ZMQ.PUB)
		    connection.bind(addr)
        }
    }
	
	def stop {
	    if(connection != null) {	        
	        connection.close()
	        connection = null
	    }
	}
	
	def publish(msg: message.Message) {
	    if(connection != null) {
	        connection.send(msg)
	    }
	}
}