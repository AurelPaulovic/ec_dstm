package com.aurelpaulovic.fiit.ec_dstm.net

import scala.collection.mutable
import org.{ zeromq => zmq }
import scala.pickling._
import json._

class DiscoveryService (private val name: String)  {
    this: ReplyConnection =>
        
    private[this] val zmqContext = zmq.ZMQ.context(1)
    val publishersMap : mutable.OpenHashMap[String, identity.Publisher] = mutable.OpenHashMap()

    protected def followerMessageResolve(msg: message.Message): Boolean = msg match {
        case message.Stop() => 
            println(s"$name got stop command")
            false
        case _ => {
            println(s"$name got unsupported command: $msg")
            true
        }
    }
    
    protected def gateMessageResolve(msg: message.Message): message.Message = msg match {
        case message.DSRegisterMessage(publisher @ identity.Publisher(name, _)) =>
            publishersMap += (name -> publisher)
            message.ACK()
        case message.DSUnregisterMessage(publisher @ identity.Publisher(name, addr)) =>
            publishersMap.get(name) match {
                case Some(identity.Publisher(`name`, `addr`)) =>
                    publishersMap.remove(name)
                    message.ACK()
                case Some(identity.Publisher(_,_)) =>
                    message.REJECT()
                case None =>
                    message.ACK()
            }
        case message.DSGetRegisteredPublishers() =>
            message.DSRegisteredPublishers(publishersMap.toMap)
        case _ => {
            message.UnsupportedMessage(msg)
        }
    }
}

