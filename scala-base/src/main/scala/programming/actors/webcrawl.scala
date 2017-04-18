package programming.actors

import java.sql.{Connection, DriverManager, SQLException}

import akka.actor._
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by I311352 on 4/18/2017.
  */
case class WebUrl(link: String)
case class ScheduledWebUrl(link: String, config: Map[String, Any])
case class CrawledWeb(link: String, domain: String, encoding: String, contentLength: Int, outlinks: Set[String])
case class Stored(link: String, outlinkCount: Int)

object MySQLUtils {
  val driverClass = "com.mysql.jdbc.Driver"
  val jdbcUrl = "jdbc:mysql://10.128.163.99:3306/web"
  val user = "root"
  val pass = "Initial0"

  try {
    Class.forName(driverClass)
  } catch {
    case e:ClassNotFoundException => throw e
    case e: Exception => throw e
  }

  @throws(classOf[SQLException])
  def getConnect: Connection ={
    DriverManager.getConnection(jdbcUrl, user, pass)
  }

  @throws(classOf[SQLException])
  def doTransaction(transactions: Set[String]): Unit = {
    val connection = getConnect
    connection.setAutoCommit(false)
    transactions.foreach{
      connection.createStatement.execute(_)
    }
    connection.commit
    connection.close
  }
}

object DatetimeUtils {
  val DEFAULT_DT_FORMAT = "yyyy-MM-dd HH:mm:ss"
  def format(timestamp: Long, format: String): String = {
    val df = new SimpleDateFormat(format)
    df.format(new Date(timestamp))
  }

  def format(timestamp: Long): String = {
    val df = new SimpleDateFormat(DEFAULT_DT_FORMAT)
    df.format(new Date(timestamp))
  }
}

object AkkaCrawlApp  {
  def main(args: Array[String]) {
    val system = ActorSystem("crawler-system") // 创建一个ActorSystem
    system.log.info(system.toString)

    val scheduleActorRef = system.actorOf(Props[ScheduleActor], name="schedule-actor")
    val storeActorRef = system.actorOf(Props[PageStoreActor], name="store-actor")
    val crawlActorRef = system.actorOf(Props[CrawlActor], name="crawl-actor")

    val links =
      """
        |http://www.heguangnan.com
        |http://www.baidu.com
      """.stripMargin
    val seeds: Seq[String] = links.split("\\s+").toSeq
    ScheduleActor.sendFeeds(crawlActorRef, seeds) // 调用ScheduleActor的伴生对象的sendFeeds，将爬虫入口seed链接发送给CrawlActor
  }
}

object ScheduleActor {
  def sendFeeds(crawlerActorRef: ActorRef, seeds: Seq[String]): Unit = {
    seeds.foreach(crawlerActorRef ! _)
  }
}

class ScheduleActor extends Actor with ActorLogging {
  val config = Map(
    "domain.black.list" -> Seq("google.com"),
    "craw.retry.times" -> 3,
    "filter.page.url.suffixes" -> Seq(".zip", ".avi")
  )
  val counter = new ConcurrentHashMap[String, Int]()
  def receive = {
    case WebUrl(url) => sender() ! ScheduledWebUrl(url, config)
    case (link: String, count: Int) => {
      counter.put(link, count)
      log.info("counter " + counter.toString)
    }
  }
}

class webcrawl {

}
