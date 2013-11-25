package com.aurelpaulovic.fiit.ec_dstm.net

import org.{ zeromq => zmq }
import concurrent.future
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait ReplyConnection extends Connection {
    protected val context: zmq.ZMQ.Context
    protected val addr: String
    
    protected def followerMessageResolve(msg: message.Message): Boolean    
    protected def gateMessageResolve(msg: message.Message): message.Message
    
    private[this] val commanderAddr = "inproc://ecdstm.connection.commander." + context.getUniqueInprocSockId
    private[this] var commander: zmq.ZMQ.Socket = null
    private[this] var connectionWorker: Future[Boolean] = null
    
    def getAddr = addr

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
                continue = followerMessageResolve(msg)
            }

            if (poller.pollin(1)) { // gate
                var msg = gate.recvStr(defaultCharset)

                var response = gateMessageResolve(msg)
                gate.send(response)
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
                val gate = context.socket(zmq.ZMQ.REP)

                try {
		            follower.connect(commanderAddr)
		            gate.bind(addr)
		            
		            ReplyConnection.this.workerLoop(follower, gate)

		            follower.unbind(commanderAddr)
		            gate.unbind(addr)
		        } catch {
		            case e: zmq.ZMQException if 
		            	zmq.ZMQ.Error.findByCode(e.getErrorCode) == zmq.ZMQ.Error.EADDRINUSE => 
		            	    println(s"Socket address '$addr' is already in use")
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