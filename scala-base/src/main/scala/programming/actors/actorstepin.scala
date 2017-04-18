package programming.actors

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.actor._

import com.typesafe.config.ConfigFactory

import scala.util.Random
import scala.util.parsing.json._

/**
  * Created by I311352 on 4/18/2017.
  */
object Start extends Serializable
object Stop extends Serializable

trait Message {
  val id: String
}

case class Shutdown(waitSecs: Int) extends Serializable
case class Heartbeat(id: String, magic:Int) extends Message with Serializable
case class Header(id: String, len: Int, encrypted: Boolean) extends Message with Serializable
case class Packet(id: String, seq: Long, content: String) extends Message with Serializable

class LocalActor extends Actor with ActorLogging {
  def receive = {
    case Start => log.info("start")
    case Stop  => log.info("stop")
    case Heartbeat(id, magic) => log.info("heartbeat " + id + magic)
    case Header(id, len, encrypted) => log.info("header " + id + len + encrypted)
    case Packet(id, seq, content) => log.info("packet " + id + seq + content)
    case _ => log.info("nothing happen")
  }
}

object LocalClient extends App {
  val system = ActorSystem("local-system")
  println(system)
  val localActorRef = system.actorOf(Props(new LocalActor()), name = "local-system")
  println(localActorRef)
  localActorRef ! Start
  localActorRef ! Heartbeat("1", 0x123)

  val data:Map[String, String] = Map()
  val dataa = data + "name" -> "Stone"

  localActorRef ! Packet("12", System.currentTimeMillis(), dataa.toString)
  localActorRef ! Stop
  system terminate()
}

class RemoteActor extends Actor with ActorLogging {
  val Succes = "SUCCESS"
  val FAILURE = "FAILUR"

  def receive = {
    case Start => log.info("RECV event:" + Start)
    case Stop => log.info("RECV event:" + Stop)
    case Shutdown(waitSecs) => {
      log.info("wait to shutdown: WaitSecs=" + waitSecs)
      Thread.sleep(waitSecs)
      log.info("Shutdown this system")
      context.system.terminate()
    }
    case Heartbeat(id, magic) => log.info("RECV heartbeat " + id + magic)
    case Header(id, len, encrypted) => log.info("RECV header " + id + len + encrypted)
    case Packet(id, seq, content) => {
      val originalSender = sender
      log.info("RECV packet:" + id + seq + content)
      originalSender ! Tuple2(id, Succes)
    }
    case _ => log.info("nothing need")
  }
}

object AkkaServerApplication extends App {
  val system = ActorSystem("remote-system", ConfigFactory.load().getConfig("MyRemoteServerSideActor"))
  val log = system.log
  log.info("Remote server actor started: " + system)
  system.actorOf(Props[RemoteActor], "remoteActor")
}

class ClientActor extends Actor with ActorLogging {
  val pa = "akka.tcp://remote-system@127.0.0.1:2552/user/remoteActor"
  val remoteActorRef = context.actorSelection(pa)
  @volatile var connect = false
  @volatile var stop = false

  def receive = {
    case Start => {
      send(Start)
      if (!connect) {
        connect = true
        log.info("actor connect " + this)
      }
    }
    case Stop => {
      send(Stop)
      connect = false
      stop = true
    }
    case header: Header => send(header)
    case hb: Heartbeat => sendWithCheck(hb)
    case pkt: Packet => sendWithCheck(pkt)
    case cmd: Shutdown => send(cmd)
    case (seq, result) => log.info("RESULT: seq=" + seq + ", result=" + result)
    case m => log.info("Unknown message: " + m)
  }

  private def sendWithCheck(cmd: Serializable): Unit = {
    while (!connect) {
      Thread.sleep(100)
      log.info("wait to be connect...")
    }
    if (!stop) {
      send(cmd)
    } else {
      log.info("actor has stopped")
    }
  }
  private def send(cmd: Serializable): Unit = {
    log.info("send command to server : " + cmd)
    try {
      remoteActorRef ! cmd
    } catch {
      case e : Exception => {
        connect = false
        log.info("try to connect by sending start command...")
        send(Start)
      }
    }
  }
}

object AkkaClientApplication extends App {
  val system = ActorSystem("client-system", ConfigFactory.load().getConfig("MyRemoteClientSideActor"))
  val log = system.log
  val clientActor = system.actorOf(Props[ClientActor], "clientActor")
  @volatile var running = true
  val hbInterval = 1000
  lazy val hbWorker = createHBWorker

  def createHBWorker: Thread = {
    new Thread("HB-WORKER") {
      override def run(): Unit = {
        while (running) {
          clientActor ! Heartbeat("HeartBeat", 123)
          Thread.sleep(hbInterval)
        }
      }
    }
  }

  def format(timeStamp: Long, format: String): String = {
    val df = new SimpleDateFormat()
    df.format(new Date(timeStamp))
  }

  def createPacket(packet: Map[String, _]): JSONObject = {
    val pkt:JSONObject = new JSONObject(packet)
    pkt
  }

  val ID = new AtomicLong(1000099)
  def nextID: Long = {
    ID.incrementAndGet()
  }

  clientActor ! Start
  Thread.sleep(2000)

  clientActor ! Header("Header", 20, encrypted = false)
  Thread.sleep(2000)

  hbWorker.start

  val DT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
  val r = Random
  val packetCount = 100
  val serviceProviders = Seq("CMCC", "AKBBC", "OLE")
  val payServiceProvicers = Seq("PayPal", "CMB", "ICBC", "ZMB", "XXB")

  def nextProvider(seq: Seq[String]): String ={
   seq(r.nextInt(seq.size))
  }

  val startWhen = System.currentTimeMillis()
  for (i <- 0 until packetCount) {
    val packet = createPacket(Map[String, Any](
      "txid" -> nextID,
      "pvid" -> nextProvider(serviceProviders),
      "txtm" -> format(System.currentTimeMillis(), DT_FORMAT),
      "payP" -> nextProvider(payServiceProvicers),
      "amount" -> 100 * r.nextFloat()
    ))
    clientActor ! Packet("PKT", System.currentTimeMillis(), packet.toString())
  }
  val finishWhen = System.currentTimeMillis()
  log.info("Finish timeTaken=" + (finishWhen - startWhen) + ", avg=" + packetCount/(finishWhen-startWhen))
  Thread.sleep(2000)
  val waitSec = hbInterval
  clientActor ! Shutdown(waitSec)
  running = false
  while (hbWorker.isAlive) {
    log.info("wait hearbeat worker to exit ...")
    Thread.sleep(100)
  }

  system.terminate()
}