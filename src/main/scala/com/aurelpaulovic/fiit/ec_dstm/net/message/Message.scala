package com.aurelpaulovic.fiit.ec_dstm.net.message

import com.aurelpaulovic.fiit.ec_dstm.{net => net}
import scala.pickling._
import json._

sealed abstract class Message

case class ACK () extends Message

case class Stop () extends Message

case class InvalidMessage (msg: String) extends Message

case class UnsupportedMessage (msg: Message) extends Message

case class DSRegisterMessage (what: net.identity.Identity) extends Message

case class DSGetRegisteredPublishers () extends Message

case class DSRegisteredPublishers (publishers: Map[String, net.identity.Publisher]) extends Message

object Message {
    implicit def message2String(m: Message): String = m.pickle.value
    
    implicit def string2Message(s: String) = {
        try {
            s.unpickle[Message]
        } catch {
            case e: scala.pickling.PicklingException =>
                println(e.getMessage())
                InvalidMessage(s)
        }
    }
}