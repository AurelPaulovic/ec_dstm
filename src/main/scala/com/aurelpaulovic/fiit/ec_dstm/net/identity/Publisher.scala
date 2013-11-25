package com.aurelpaulovic.fiit.ec_dstm.net.identity

case class Publisher (val name: String, val addr: String) extends Identity

object Publisher {
    private[this] var i: Integer = 0 
    
    def apply(addr: String) = new Publisher(generateName, addr)
    
    private[this] def generateName(): String = {
        this.synchronized {
            var name = s"_publisher_$i"
            i += 1
            name
        }
    }
}