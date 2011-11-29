package co.za.countdown.source

import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import scala.xml.parsing.NoBindingFactoryAdapter
import org.xml.sax.InputSource
import xml.{NodeSeq, Node}
import java.io.InputStream
import org.joda.time.format.DateTimeFormat
import collection.mutable.ListBuffer
import org.joda.time.{DateTime, LocalDate}

/**
 * User: dawidmalan
 * Date: 2011/11/29
 * Time: 8:42 AM
 */

object GamesWebScrape {
  val url = "http://www.btgames.co.za/bt/release.asp"
  val parserFactory = new SAXFactoryImpl
  val parser = parserFactory.newSAXParser
  val adapter = new NoBindingFactoryAdapter
  val monthYearFormat = DateTimeFormat.forPattern("MMMM, yyyy")
  val dayMonthFormat = DateTimeFormat.forPattern("dd MMMM")

  def getOnline = {
     val source = new InputSource(url)
    parseDates(adapter.loadXML(source, parser))
  }
  def getDateItems(is: InputStream) = {
    val source = new InputSource(is)
    parseDates(adapter.loadXML(source, parser))
  }

  def parseDates(node: Node): List[(String, DateTime)] = {
    var currentYM: Option[DateTime] = None
    val items = new ListBuffer[(String, DateTime)]

    val usable = ((node \\ "table").drop(2) \ "tr") dropWhile ((tr: Node) => parseYM((tr \ "td").head).isEmpty)

    usable foreach ((n: NodeSeq) => {
      if ((n \ "td").length == 1) {
        currentYM = parseYM(n \ "td")
      }
      else parseItem(n, currentYM) foreach {
        item: (String, DateTime) => items.append(item)
      }
    })
    items.toList
  }

  def parseYM(n: NodeSeq): Option[DateTime] = {
    try {
      val dateText = n.text.replace(32.toChar, ' ').replace(160.toChar, ' ').trim()
      Some(monthYearFormat.parseDateTime(dateText))
    }
    catch {
      case e: Exception => None
    }
  }

  def parseItem(n: NodeSeq, ymo: Option[DateTime]): Option[(String, DateTime)] = {
    val cols = (n \ "td").map(_.text).toList
    ymo match {
      case Some(ym) => {
        val title = cols(0).replaceAll("""[\s ]{2,}""", " ")
        try {
          val dayMonth = dayMonthFormat.parseDateTime(cols(6)).dayOfMonth()
          Some( title -> ym.withDayOfMonth(dayMonth.get()))
        } catch {
          case e: Exception => Some( title -> ym)
        }
      }
      case _ => None
    }
  }
}
object Main extends App{
     GamesWebScrape.getOnline foreach(println)
}
