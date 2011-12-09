package co.za.countdown.serve


import unfiltered.request._
import unfiltered.response._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonDSL._
import co.za.countdown.counter.CountdownService
import java.net.URL
import org.bson.types.ObjectId
import unfiltered.response.ResponseHeader._
import co.za.countdown._
import org.joda.time.{DateTimeZone, DateTime}


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
    case Path(Seg("tags" :: q)) => searchTags(q.headOption)
    case GET(Path(Seg("countdown" :: "random" :: Nil))) => randomResponse
    case Path(Seg("countdown" :: "new" :: Nil)) & Params(params) => newResponse(params)
    case Path(Seg("countdown" :: "upsert" :: Nil)) & Params(params) => upsertResponse(params)
    case Path(Seg("countdown" :: "search" :: Nil)) & Params(params) => searchResponse(params)
    case GET(Path(Seg("countdown" :: q :: Nil))) => countdownResponse(q)
    case GET(Path(Seg("countdownlist" :: Nil))) => allCountdownsResponse
    case _ => errorResponse("Invalid request")
  }

  def errorResponse(msg: String) = JsonContent ~> ResponseString(compact(render("error" -> msg)))

  def randomResponse = {
    val all = CountdownService.retrieveAll
    JsonContent ~> ResponseString(write(MillisCountdown(all((math.random * (all.length - 1)).toInt))))
  }

  def newResponse(params: Map[String, Seq[String]]) = {
    addResponse(params, (name, eventMillis: Long, tags) => {
      CountdownService.insertCountdown(AspiringCountdown(name, new DateTime(eventMillis.toLong, DateTimeZone.UTC), tags.split(",").toList)) match {
        case Some(c: Countdown) => JsonContent ~> ResponseString(write(MillisCountdown(c)))
        case _ => errorResponse("Could not persist")
      }
    })
  }

  def upsertResponse(params: Map[String, Seq[String]]) = {
    addResponse(params, (name, eventMillis: Long, tags) => {
      CountdownService.upsertCountdown(AspiringCountdown(name, new DateTime(eventMillis.toLong, DateTimeZone.UTC), tags.split(",").toList))
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
  
  def searchTags(q: Option[String]) = {
    JsonContent ~> ResponseString(compact(render("tags" -> CountdownService.searchTags(q))))
  }

  def allCountdownsResponse = {
    JsonContent ~> ResponseString(write(Countdowns(CountdownService.retrieveAll.map(searchResultMap))))
  }
}
case class Countdowns(countdowns: List[MillisCountdown])




