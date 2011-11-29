package co.za.countdown.serve


import unfiltered.request._
import unfiltered.response._
import unfiltered.request.GET
import co.za.countdown.counter.CountdownService
import org.joda.time.DateTime

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:32 PM
 */

object ServeCountdowns {
   val countdowns = unfiltered.filter.Planify {
    case GET(Path(Seg("countdown" :: q :: Nil))) => {

       CountdownService.retrieveByName(q) match{
         case Some( item: (String, DateTime)) => {
             ResponseString("Countdown for: " + item._1 + " " + (item._2.getMillis - System.currentTimeMillis()).toString  +" millis to go!")
         }
         case None => ResponseString("No countdown found for "+q)
       }
    }
    case _ => ResponseString("404")
  }

}