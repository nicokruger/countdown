package co.za.countdown.serve


import unfiltered.response._
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import net.liftweb.json.JsonDSL._
import co.za.countdown.counter.CountdownService
import org.joda.time.DateTime
import org.bson.types.ObjectId
import co.za.countdown._

object ServeCountdowns {
  val nameField = "name"
  implicit val formats = Serialization.formats(NoTypeHints)
  val searchResultMap = (countdown:Countdown) => MillisCountdown(countdown)

  def errorResponse(msg: String) = JsonContent ~> ResponseString(compact(render("error" -> msg)))

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

//    val results = CountdownService.search(name, startMillis, endMillis, tags).map(searchResultMap).toList
    JsonContent ~> ResponseString(write(Countdowns(List())))
  }

}
case class Countdowns(countdowns: List[MillisCountdown])




