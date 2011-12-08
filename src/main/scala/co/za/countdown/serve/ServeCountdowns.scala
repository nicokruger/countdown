package co.za.countdown.serve


import unfiltered.response._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonDSL._
import co.za.countdown.counter.CountdownService
import org.joda.time.DateTime
import java.net.URL
import org.bson.types.ObjectId
import unfiltered.response.ResponseHeader._
import co.za.countdown._
import unfiltered.request._
import collection.mutable.ListBuffer
import scala.collection.JavaConversions._

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:32 PM
 */

object ServeCountdowns {
  val nameField = "name"
  implicit val formats = Serialization.formats(NoTypeHints)
  val searchResultMap = (countdown:Countdown) => MillisCountdown(countdown)

  val countdowns = unfiltered.filter.Planify {
    case GET(Path(Seg("countdown" :: "random" :: Nil))) => randomResponse
    case Path(Seg("countdown" :: "new" :: Nil)) & Params(params) => newResponse(params)
    case Path(Seg("countdown" :: "upsert" :: Nil)) & Params(params) => upsertResponse(params)
    case Path(Seg("countdown" :: "search" :: Nil)) & Params(params) => searchResponse(params)
    case GET(Path(Seg("countdownlist" :: Nil))) => allCountdownsResponse
    case GET(Path("/random")) & Accepts.Html(_) => htmlResponse(List(randomCountdown))
    case req @ GET(Path("/")) & Params(params) => {

      val s = req.headers("Accept").mkString(",")

      if (s.contains("application/json")) {
        countdownResponse(params.mkString)
      } else if (s.contains("text/html")) {

        if (params.size > 0) {
          val id:String = params.keys.first.values.foldLeft("")( (x,y) => x + y)

          CountdownService.retrieveById(new ObjectId(id)) match {
            case Some(item: Countdown) => {
              HtmlContent ~> htmlResponse(List(item))
            }
            case None => errorResponse("Countdown was not found ")
          }
        } else {
          htmlResponse(CountdownService.retrieveAll)
        }
      } else {
        ResponseString("Invalid type")
      }
    }
    case GET(Path("/")) & Accepts.Html(_) => htmlResponse(CountdownService.retrieveAll)
    //case
    //case GET(Path(Seg(q :: Nil))) & Accepts.Json(_) => countdownResponse(q)

    //case x @ Accepts.Html(_) => x match {
//       case GET(Path(Seg("_" :: Nil))) => indexResponse
 //   }
    //case x @ Accepts.Html(_) => x match {
    //  case GET(Path(Seg("" :: Nil))) => indexResponse
    //}
    //case _ => require().
  }

  def htmlResponse(countdowns:List[Countdown]) = {
    val c = for (countdown <- countdowns) yield new CountdownInfo(countdown.name, countdown.url, countdown.eventDate.getMillis)

    val javaCountdowns = asList(ListBuffer(c: _*))
    HtmlContent ~> ResponseString(CountdownHtml.getHtml(javaCountdowns))
  }

  def errorResponse(msg: String) = JsonContent ~> ResponseString(compact(render("error" -> msg)))

  def randomCountdown = {
    val all = CountdownService.retrieveAll
    all((math.random * (all.length - 1)).toInt)
  }
  def randomResponse = {
    val all = CountdownService.retrieveAll
    JsonContent ~> ResponseString(write(MillisCountdown(randomCountdown)))
  }

  def newResponse(params: Map[String, Seq[String]]) = {
    addResponse(params, (name, eventMillis: Long, tags) => {
      CountdownService.insertCountdown(AspiringCountdown(name, new DateTime(eventMillis.toLong), tags.split(",").toList)) match {
        case Some(c: Countdown) => JsonContent ~> ResponseString(write(MillisCountdown(c)))
        case _ => errorResponse("Could not persist")
      }
    })
  }

  def upsertResponse(params: Map[String, Seq[String]]) = {
    addResponse(params, (name, eventMillis: Long, tags) => {
      CountdownService.upsertCountdown(AspiringCountdown(name, new DateTime(eventMillis.toLong), tags.split(",").toList))
      ResponseString(compact(render("success" -> "Countdown upserted")))
    })
  }

  def addResponse(params: Map[String, Seq[String]], paramHandler: (String, Long, String) => ResponseFunction[Any]) = {
    val name = params.getOrElse(nameField, Nil).headOption
    val eventMillis = params.getOrElse("eventDate", Nil).map(_.toLong).headOption
    val tags = params.getOrElse("tags", Nil).headOption
    (name, eventMillis, tags) match {
      case (Some(name: String), Some(eventMillis: Long), Some(tags: String)) => paramHandler(name, eventMillis, tags)
      case _ => errorResponse("your params are broken")
    }
  }

  def searchResponse(params: Map[String, Seq[String]]) = {
    val name = params.getOrElse(nameField, Nil).headOption
    val startMillis = params.getOrElse("start", Nil).map(_.toLong).headOption
    val endMillis = params.getOrElse("end", Nil).map(_.toLong).headOption
    val tags = params.getOrElse("tags", Nil).flatMap(_.split(",")).toList

    val results = CountdownService.search(name, startMillis, endMillis, tags).map(searchResultMap).toList
    JsonContent ~> ResponseString(write(Countdowns(results)))
  }

  def countdownResponse(q: String) = {
    CountdownService.retrieveById(new ObjectId(q)) match {
      case Some(item: Countdown) => {
        JsonContent ~> ResponseString(write(MillisCountdown(item)))
      }
      case None => errorResponse("Countdown was not found ")
    }
  }

  def allCountdownsResponse = {
    JsonContent ~> ResponseString(write(Countdowns(CountdownService.retrieveAll.map(searchResultMap))))
  }


}
case class Countdowns(countdowns: List[MillisCountdown])




