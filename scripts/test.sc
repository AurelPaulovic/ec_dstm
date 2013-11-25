import scala.runtime.AbstractPartialFunction

abstract trait Command extends PartialFunction[Command, Unit] {
  def isDefinedAt(c: Command) = c.equals(this)
}

abstract trait Commandable {
  val commands: List[Command]

  val default: PartialFunction[Command, Unit] = {
    case _ => println("unsupported command")
  }

  lazy val execute = {
    commands.foldRight(default) {
      (p1: PartialFunction[Command, Unit], p2: PartialFunction[Command, Unit]) => p1.orElse(p2)
    }
  }
}

class MyClass () {
  this: Commandable =>
}

case class A () extends Command {
  override def apply(v1: Command) {
    println("A got Apply")
  }
}

case class B () extends Command {
  override def apply(v1: Command) {
    println("B got Apply")
  }
}

case class C () extends Command {
  override def apply(v1: Command) {
    println("C got Apply")
  }
}

object test {
    println("xaxs")

    var x = new MyClass() with Commandable {
      val commands = List(A())
    }
}