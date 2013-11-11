package com.aurelpaulovic.fiit.ec_dstm.net.commands

abstract trait Commander {
  val commands: List[Command]

  val defaultExecute: PartialFunction[Command, Unit] = {
    case _ => println("unsupported command")
  }

  lazy val execute = {
    commands.foldRight(defaultExecute) {
      (p1: PartialFunction[Command, Unit], p2: PartialFunction[Command, Unit]) => p1.orElse(p2)
    }
  }
}