package com.aurelpaulovic.fiit.ec_dstm.net

import scala.collection.mutable
import org.{ zeromq => zmq }

class DiscoveryService (private val name: String)  {
    this: ReplyConnection =>
        
    private[this] val zmqContext = zmq.ZMQ.context(1)
    val nodesMap : mutable.OpenHashMap[Int, ReplyConnection] = mutable.OpenHashMap()

    protected def followerMessageResolve(msg: String): Boolean = {
        println(name + " got command: " + msg)
        
		if (msg.equals("stop")) {
			println("stopping")
			return false
		}
		
		true
    }
    
    protected def gateMessageResolve(msg: String): String = {
        println(name + " got msg: " + msg)
        
		"respose to msg: " + msg
    }
}

