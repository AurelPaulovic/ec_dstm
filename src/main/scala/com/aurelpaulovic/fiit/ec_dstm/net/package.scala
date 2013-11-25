package com.aurelpaulovic.fiit.ec_dstm

import org.zeromq.ZMQ.Context

package object net {
	implicit def context2RichContext(c: Context) = new RichContext(c)
	
	implicit def replyConnection2Addres(rc: ReplyConnection) = rc.getAddr
	
	val defaultCharset = java.nio.charset.Charset.forName("UTF-8")
}