package com.aurelpaulovic.fiit.ec_dstm.net.commands

case class Stop () extends Command {
	override def apply(c: Command) {
	    println("apply Stop")
	}
}