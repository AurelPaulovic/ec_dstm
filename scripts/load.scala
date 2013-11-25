import org.{zeromq => zmq}
import com.aurelpaulovic.fiit.ec_dstm._

val c = zmq.ZMQ.context(1)
val ds = new net.DiscoveryService("Discovery") with net.ReplyConnection {
    val addr = "tcp://127.0.0.1:5555"
    val context = c 
}