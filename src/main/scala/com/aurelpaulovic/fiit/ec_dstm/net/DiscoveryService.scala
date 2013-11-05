package com.aurelpaulovic.fiit.ec_dstm.net

import scala.collection.mutable
import org.{ zeromq => zmq }

object DiscoveryService {
    private[this] val zmqContext = zmq.ZMQ.context(1)
    val nodesMap : mutable.OpenHashMap[Int, Connection] = mutable.OpenHashMap()

    private[this] val socket : zmq.ZMQ.Socket = zmqContext.socket(zmq.ZMQ.REP)
    val addr = "tcp://127.0.0.1:5555"
        
    private[this] val commandSocket : zmq.ZMQ.Socket = zmqContext.socket(zmq.ZMQ.REQ)
    val commandAddr = "icp://ec_dstm.DiscoveryService.command.icp"

    private[this] var bound = false

    def start {
        try {
            socket.bind(addr)
            socket.bind(commandAddr)
            bound = true
        } catch {
            case e: zmq.ZMQException if 
            	zmq.ZMQ.Error.findByCode(e.getErrorCode) == zmq.ZMQ.Error.EADDRINUSE => 
            	    println(s"Socket address '$addr' is already in use")
        }
    }

    def stop {
        if(bound) {
        	socket.unbind(addr)
        	bound = false
        }
    }
}
