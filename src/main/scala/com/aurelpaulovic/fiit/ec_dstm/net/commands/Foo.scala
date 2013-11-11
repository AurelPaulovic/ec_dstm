package com.aurelpaulovic.fiit.ec_dstm.net.commands

case class Foo () extends Command {
	override def apply(c: Command) {
	    println("apply foo")
	}
}