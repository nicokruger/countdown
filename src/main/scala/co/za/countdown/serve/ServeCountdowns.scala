package co.za.countdown.serve


import unfiltered.request._
import unfiltered.response._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonDSL._
import co.za.countdown.counter.CountdownService
import org.joda.time.DateTime
import co.za.countdown.Countdown
import java.net.URL
import org.bson.types.ObjectId
import unfiltered.response.ResponseHeader._

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:32 PM
 */

object ServeCountdowns {
  implicit val formats = Serialization.formats(NoTypeHints)

  val countdowns = unfiltered.filter.Planify {
    case GET(Path(Seg("countdown" :: q :: Nil))) => {

      CountdownService.retrieveById(new ObjectId(q)) match {
        case Some(item: Countdown) => {
          JsonContent ~> ResponseString(write(MillisCountdown(item)))
        }
        case None =>  JsonContent ~> ResponseString(compact(render("error" -> "No countdown found for " + q)))
      }
    }
    case GET(Path(Seg("countdownlist" :: Nil))) => {
      JsonContent ~> ResponseString(compact(render("countdowns" -> CountdownService.retrieveAll.map(
        (cd:Countdown) => ( ("label" -> cd.name) ~ ("url" -> cd.url) ) ))))
    }
    case _ => JsonContent ~> ResponseString(compact(render("error" -> "Invalid request")))
  }
}

//temp workaround
case class MillisCountdown(name: String, url: String,  eventDate: Long)

object MillisCountdown {
  def apply(countdown: Countdown):MillisCountdown = MillisCountdown(countdown.name, countdown.url, countdown.eventDate.getMillis)
}


