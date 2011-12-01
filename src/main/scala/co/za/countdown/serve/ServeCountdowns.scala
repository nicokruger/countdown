package co.za.countdown.serve


import unfiltered.request._
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


/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:32 PM
 */

object ServeCountdowns {
  implicit val formats = Serialization.formats(NoTypeHints)
  def errorResponse(msg: String) = JsonContent ~> ResponseString(compact(render("error" -> msg)))
  val searchResultMap = (cd:Countdown) => ( ("label" -> cd.name) ~ ("url" -> cd.url) )

  val countdowns = unfiltered.filter.Planify {
    case GET(Path(Seg("countdown" :: "random" :: Nil))) => {
       val all = CountdownService.retrieveAll
     JsonContent ~> ResponseString(write(MillisCountdown(all( (math.random * (all.length - 1)).toInt))))
    }
    //http://localhost:55555/countdown/new?label=toffie&eventDate=1332194400000&tags=appel,peer
    case Path(Seg("countdown" ::"new" :: Nil))  & Params(params)  => {
        val label = params.getOrElse("label",Nil).headOption
       val eventMillis = params.getOrElse("eventDate", Nil).map(_.toLong).headOption
       val tags = params.getOrElse("tags", Nil).headOption
       println("HERE ARE THE PARAMS "+label+ " e "+eventMillis +" tags "+tags)
      (label, eventMillis, tags) match {
        case (Some(label:String), Some(eventMillis:Long), Some(tags: String) ) => {
          CountdownService.insertCountdown(AspiringCountdown(label, new DateTime(eventMillis.toLong), tags.split(",").toList )) match {
            case Some(c:Countdown) =>  JsonContent ~> ResponseString(write(MillisCountdown(c)))
            case _ => errorResponse("Could not persist")
          }
        }
        case _ => errorResponse("your params are broken")
      }
    }
    case Path(Seg("countdown" :: "search" :: Nil)) & Params(params) => {

       val label = params.getOrElse("label",Nil).headOption
       val startMillis = params.getOrElse("start", Nil).map(_.toLong).headOption
       val endMillis = params.getOrElse("end", Nil).map(_.toLong).headOption
       val tags = params.getOrElse("tags", Nil).flatMap(_.split(",")).toList

       val results = CountdownService.search(label, startMillis, endMillis, tags).map(searchResultMap ).toList
      JsonContent ~> ResponseString(compact(render("countdowns" -> results)))
    }
    case GET(Path(Seg("countdown" :: q :: Nil))) => {

      CountdownService.retrieveById(new ObjectId(q)) match {
        case Some(item: Countdown) => {
          JsonContent ~> ResponseString(write(MillisCountdown(item)))
        }
        case None =>  errorResponse("Countdown was not found ")
      }
    }
    case GET(Path(Seg("countdownlist" :: Nil))) => {
      JsonContent ~> ResponseString(compact(render("countdowns" -> CountdownService.retrieveAll.map(
        searchResultMap ))))
    }

    case _ =>  errorResponse("Invalid request")
  }
}




