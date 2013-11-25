package com.aurelpaulovic.fiit.ec_dstm.net

import org.{ zeromq => zmq }
import scala.collection.mutable
import concurrent.future
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SubscriberConnection (protected val context: zmq.ZMQ.Context) extends Connection {
	private[this] val publishersMap: mutable.OpenHashMap[String, identity.Publisher] = mutable.OpenHashMap()
	
	private[this] val commanderAddr = "inproc://ecdstm.connection.commander." + context.getUniqueInprocSockId
    private[this] var commander: zmq.ZMQ.Socket = null
    private[this] var connectionWorker: Future[Boolean] = null
    
    def addPub(pub: identity.Publisher) {
	    if (connectionWorker != null && !connectionWorker.isCompleted) {
        	commander.send(message.SubAddPub(pub))
        }
	}
	
	def removePub(pub: identity.Publisher) {
	    if (connectionWorker != null && !connectionWorker.isCompleted) {
        	commander.send(message.SubRemPub(pub))
        }
	}
	
	def followerMessageResolve(msg: message.Message, follower: zmq.ZMQ.Socket): Boolean = msg match {
	    case message.Stop() => 
            println("got stop command")
            false
	    case message.SubAddPub(pub) =>
	        println("got addPub command")
	        SubscriberConnection.this.publishersMap.get(pub.name) match {
	            case Some(current) if current.addr != pub.addr =>
	                println("had another already")
	                follower.disconnect(current.addr)
	                SubscriberConnection.this.publishersMap += (pub.name -> pub)
	                follower.connect(pub.addr)
	                true
	            case None => 
	                println("brand new")
	                SubscriberConnection.this.publishersMap += (pub.name -> pub)
	                follower.connect(pub.addr)
	                follower.subscribe("".getBytes)
	                true
	            case Some(current) if current.addr == pub.addr =>
	                println("already connected")
	                true
	        }
	        
	        println("current connected publishers: ")
	        println(SubscriberConnection.this.publishersMap.mkString(","))
	        println("---")
	        
	        true
	    case message.SubRemPub(pub) =>
	        println("got remPub command")
	        SubscriberConnection.this.publishersMap.get(pub.name) match {
	            case Some(current) if current.addr != pub.addr =>
	                println("had different")
	                true
	            case None => 
	                println("no such")
	                true
	            case Some(current) if current.addr == pub.addr =>
	                println("disconnecting")
	                SubscriberConnection.this.publishersMap.remove(pub.name)
	                follower.disconnect(pub.addr)
	                true
	        }
	        
	        println("current connected publishers: ")
	        println(SubscriberConnection.this.publishersMap.mkString(","))
	        println("---")
	        
	        true
        case _ => {
            println("got unsupported command: $msg")
            true
        }
	} 
	
	def workerLoop(follower: zmq.ZMQ.Socket, gate: zmq.ZMQ.Socket) {
        val poller = new zmq.ZMQ.Poller(2)
        
        // !! has to specify register events mask or pool will not block and we will end up in a busy loop
        poller.register(follower, zmq.ZMQ.Poller.POLLIN) 
        poller.register(gate, zmq.ZMQ.Poller.POLLIN)

        var continue = true
        do {
            poller.poll()

            if (poller.pollin(0)) { // command
                
                var msg = follower.recvStr(defaultCharset)
                continue = followerMessageResolve(msg, follower)
            }

            if (poller.pollin(1)) { // gate
            	var msg = gate.recvStr(defaultCharset)
				println(s"got msg: $msg")
            }
        } while (continue)

        poller.unregister(follower)
        poller.unregister(gate)
    }
	
    def start {
        if (connectionWorker == null && commander == null) {
            commander = context.socket(zmq.ZMQ.PAIR)
            commander.bind(commanderAddr)

            connectionWorker = future {
                val follower = context.socket(zmq.ZMQ.PAIR)
                val gate = context.socket(zmq.ZMQ.SUB)

                try {
		            follower.connect(commanderAddr)
		            
		            workerLoop(follower, gate)
		            
		            follower.unbind(commanderAddr)
		        } catch {
		            case e: zmq.ZMQException if 
		            	zmq.ZMQ.Error.findByCode(e.getErrorCode) == zmq.ZMQ.Error.EADDRINUSE => 
		            	    println(s"Socket address '$commanderAddr' is already in use")
		            	    return
		        } finally {
		            follower.close()
		            gate.close()
		        }

            	true
            }
        }
    }

    def stop {
        if (connectionWorker != null) {
            if(!connectionWorker.isCompleted) {
            	commander.send(message.Stop())
            	Await.ready(connectionWorker, Duration.Inf)
            }

            commander.close()
            commander = null
            connectionWorker = null
        }
    }
}