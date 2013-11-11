package com.aurelpaulovic.fiit.ec_dstm.net.commands

abstract trait Command extends PartialFunction[Command, Unit] {
    def isDefinedAt(c: Command) = c.equals(this)
}