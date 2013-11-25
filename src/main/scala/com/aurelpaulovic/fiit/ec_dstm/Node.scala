package com.aurelpaulovic.fiit.ec_dstm

import scala.collection.mutable

class Node (private val id : Int) {
  val connections: mutable.ListBuffer[net.ReplyConnection] = mutable.ListBuffer()
  
  override def toString() = {
    "Node " + id
  }
} 

object Node {
  private[this] var id = 0;
  
  def apply() = {
    id += 1
    new Node(id)
  }
}