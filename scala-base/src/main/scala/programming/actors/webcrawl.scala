package programming.actors

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.sql.{Connection, DriverManager, SQLException}

import akka.actor._
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.{ConcurrentHashMap, ForkJoinPool, LinkedBlockingDeque}

import scala.concurrent.{ExecutionContext, Future}
import java.net.URL

import akka.event.Logging

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
        |https://10.128.163.99/
      """.stripMargin
    val seeds: Seq[String] = links.split("\\s+").toSeq
    ScheduleActor.sendFeeds(crawlActorRef, seeds)
  }
}
class CrawlActor extends Actor with ActorLogging {
  private val schedualActor = context.actorOf(Props[ScheduleActor], "schedule-actor")
  private val pageStoreActor = context.actorOf(Props[PageStoreActor], "store-actor")
  private val q = new LinkedBlockingDeque[String]()

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())
  def receive = {
    case link:String => {//&& link.startsWith("http://")
      if (link != null ) {
        log.info("checked " + link)
        schedualActor ! WebUrl(link)
      }
    }
    case ScheduledWebUrl(link, _) => {
      var crawledWeb : CrawledWeb = null
      val crawlFuture = Future {
        try {
          var encoding = "utf-8"
          var outLinks:Set[String] = Set[String]()
          val u = new URL(link)
          val domain = u.getHost
          val uc = u.openConnection().asInstanceOf[HttpURLConnection]
          uc.setConnectTimeout(5000)
          uc.connect()
          if (uc.getResponseCode == 200) {
            // page encoding
            if (uc.getContentEncoding != null) {
              encoding = uc.getContentEncoding
            }

            if (uc.getContentLength > 0) {
              val in = uc.getInputStream
              val buffer = Array.fill[Byte](512)(0)
              val baos = new ByteArrayOutputStream
              var bytesRead = in.read(buffer)
              while (bytesRead > -1) {
                baos.write(buffer, 0, bytesRead)
                bytesRead = in.read(buffer)
              }
              outLinks = extractOutLinks(link, baos.toString(encoding))
              baos.close
            }
            log.info("Page: link=" + link + ", encoding=" + encoding + ", outlinks=" + outLinks)
            CrawledWeb(link, domain, encoding, uc.getContentLength, outLinks)
          }
        } catch {
          case e :Throwable => {
            log.error("Crawl error: " + e.toString)
            e
          }
        }
      }
      crawlFuture.onSuccess {
        case crawledWeb:CrawledWeb => {
          log.info("Succeed to crawl: link=" + link + ", crawledWeb=" + crawledWeb)
          if (crawledWeb != null) {
            pageStoreActor ! crawledWeb
            log.info("sent crawled data to store actor")
          }
        }
      }
      crawlFuture.onFailure {
        case e:Throwable => log.error("fail to crawl " + e.toString)
      }
    }
    case Stored(link, count) => {
      q.remove(link)
      schedualActor ! Tuple2(link, count)
    }
  }

  def extractOutLinks(parentsURL: String, content:String) : Set[String] = {
    val outlinks = "href\\s*=\\s*\"([^\"]+)\"".r.findAllMatchIn(content).map { m =>
      var url = m.group(1)
      if (!url.startsWith("http")) {
        url = new URL(new URL(parentsURL), url).toExternalForm
      }
      url
    }.toSet
    outlinks.filter(url => !url.isEmpty && (url.endsWith("html") || url.endsWith("htm")))
  }


}
class PageStoreActor extends Actor with ActorLogging {
  implicit val ec:ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool())
  var crawlerRef = context.actorOf(Props[CrawlActor], name="crawl-actor")

  override def receive = {
    case CrawledWeb(link, domain, encoding, contentLength, outlinks) => {
      val future = Future {
        var sqls = Set[String]()
        try {
          val createTime = DatetimeUtils.format(System.currentTimeMillis)
          val sql = "INSERT INTO web_link VALUES ('" + link + "','" + domain + "','" + encoding + "',"
                    + contentLength + ",'" + createTime + "')"
          log.info("Link SQL: " + sql)
          sqls += sql
          var outlinksSql = "INSERT INTO web_outlink VALUES "
          outlinksSql += outlinks.map("('" + link + "','" + _ + "','" + createTime + "')").mkString(",")

          log.info("Outlinks SQL: " + outlinksSql)
          sqls += sql
          MySQLUtils.doTransaction(sqls)
          (link, outlinks.size)
        } catch {
          case e :Throwable => throw e
        }
      }
      future.onSuccess {
        case (link: String, outlinkCount: Int) => {
          log.info("SUCCESS: link=" + link + ", outlinkCount=" + outlinkCount)
          crawlerRef ! Stored(link, outlinkCount)
        }
      }
      future.onFailure {
        case e: Throwable  => throw e
      }
    }

    case _ => ""
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
