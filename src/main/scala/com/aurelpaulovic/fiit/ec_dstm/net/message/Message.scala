package com.aurelpaulovic.fiit.ec_dstm.net.message

import com.aurelpaulovic.fiit.ec_dstm.{net => net}
import scala.pickling._
import json._

sealed abstract class Message

case class DSRegisterMessage (what: net.identity.Identity)


object Message {
    implicit def message2String(m: Message) = m.pickle
}