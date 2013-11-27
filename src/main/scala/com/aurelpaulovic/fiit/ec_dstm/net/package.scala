package com.aurelpaulovic.fiit.ec_dstm

import org.zeromq.ZMQ.Context
import scala.pickling._
import json._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

package object net {
	implicit def context2RichContext(c: Context) = {
	    if(c == null) throw new NullPointerException("calling implicit context2RichContext with null context")
	    new RichContext(c)
	}
	
	implicit def replyConnection2Addres(rc: ReplyConnection) = rc.getAddr
	
	val defaultCharset = java.nio.charset.Charset.forName("UTF-8")
	
	implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(50))
}