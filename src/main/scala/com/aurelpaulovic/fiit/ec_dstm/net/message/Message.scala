package com.aurelpaulovic.fiit.ec_dstm.net.message

import com.aurelpaulovic.fiit.ec_dstm.{net => net}
import scala.pickling._
import json._

abstract class Message

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