package com.aurelpaulovic.fiit.ec_dstm.net

import org.{zeromq => zmq}
import RichContext.context2RichContext
import concurrent.future
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Connection (private val context: zmq.ZMQ.Context, private val addr: String) {
    private[this] val commanderAddr = "inproc://ecdstm.connection.commander." + context.getUniqueInprocSockId
    private[this] var commander: zmq.ZMQ.Socket = null
    private[this] var connectionWorker: Future[Boolean] = null
    
    def start { 
        if(connectionWorker == null && commander == null) {
            commander = context.socket(zmq.ZMQ.PAIR)
            commander.bind(commanderAddr)

            connectionWorker = future {
		    	val follower = context.socket(zmq.ZMQ.PAIR)
		    	val gate = context.socket(zmq.ZMQ.REP)
		    	val poller = new zmq.ZMQ.Poller(2)
		    	
		    	follower.connect(commanderAddr)
		    	gate.bind(addr)
		    	
		    	poller.register(follower)
		    	poller.register(gate)
		    	
		    	var continue = true
		    	do {
		    	    poller.poll()
		    	    
		    	    if(poller.pollin(0)) { // command
		    	        var msg = follower.recvStr
		    	        println(s"follower got: $msg")
		    	        
		    	        if(msg.equals("stop")) {
		    	            println("stopping")
		    	            continue = false
		    	        }
		    	    }
		    	    
		    	    if(poller.pollin(1)) { // gate
		    	        var msg = gate.recvStr
		    	        println(s"gate got: $msg")
		    	        
		    	        gate.send("got your msg")
		    	    }
		    	} while (continue)
		    	
		    	poller.unregister(follower)
		    	poller.unregister(gate)
		    	
		    	follower.unbind(commanderAddr)
		    	gate.unbind(addr)
		    	
		    	follower.close()
		    	gate.close()
		    	true
	        }
        }
    }
    
    def stop {
        if(connectionWorker != null) {
            commander.send("stop")
            
            Await.ready(connectionWorker, Duration.Inf)
            
            commander.close()
            commander = null
            connectionWorker = null
        }
    }
}