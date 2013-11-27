package com.aurelpaulovic.fiit.ec_dstm

import org.{zeromq => zmq}
import scala.collection.mutable

class Node (private val id : Int) {
    var dsReq: net.RequestConnection = null
    var pub: net.PublisherConnection = null
    var sub: net.SubscriberConnection = null
    
  override def toString() = {
    "Node " + id
  }
  
  def connect(c: zmq.ZMQ.Context, ds: net.DiscoveryService with net.ReplyConnection, addr: String) {
      pub = new net.PublisherConnection(c, addr)
      pub.start

      println(this + ": starting publisher " + pub.identity)
      
      dsReq = new net.RequestConnection(c, ds)
      dsReq.start
      dsReq.ask(net.message.DSRegisterIdentity(pub.identity))
      
      sub = new net.SubscriberConnection(c)
      sub.start
      
      dsReq.ask(net.message.DSGetRegisteredPublishers()) match {
          case None =>
              println(this + ": no other publishers yet")
          case Some(net.message.DSRegisteredPublishers(publishers)) =>
              for(remPub <- publishers
                  if remPub != pub.identity) {
                  	println(this + ": listening to remote publisher " + remPub)
            	  	sub.addPub(remPub)
              }
          case _ =>
              println(this + ": got some error 1")
      }
      
      println(this + ": connected and ready")
  }
  
  def disconnect() {
      dsReq.ask(net.message.DSUnregisterIdentity(pub.identity))
      pub.stop
      sub.stop
      dsReq.stop
      
      println(this + ": disconnected")
  }
  
  def shout(msg: String) {
      pub.publish(net.message.TextMessage(msg))
  }
} 

object Node {
  private[this] var id = 0;
  
  def apply() = {
    id += 1
    new Node(id)
  }
}