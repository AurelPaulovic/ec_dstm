package com.aurelpaulovic.fiit.ec_dstm.net

import org.zeromq.ZMQ.Context

class RichContext (private val c: Context) {
    def getUniqueInprocSockId = RichContext.getUniqueIdForContext(c)
}

object RichContext {
    private[this] var contextsIds: Map[Context, Int] = Map() 
    
    implicit def context2RichContext(c: Context) = new RichContext(c)
    
    protected def getUniqueIdForContext(c: Context) = {
        this.synchronized {
            var id = contextsIds.getOrElse(c, 0) + 1
            contextsIds = contextsIds + (c -> id)
            id
        }
    }
}