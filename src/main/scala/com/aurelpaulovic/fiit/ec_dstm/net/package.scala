package com.aurelpaulovic.fiit.ec_dstm

import org.zeromq.ZMQ.Context

package object net {
	implicit def context2RichContext(c: Context) = new RichContext(c)
	
	val defaultCharset = java.nio.charset.Charset.forName("UTF-8")
}