package co.za.countdown

import org.joda.time.DateTime

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 9:30 PM
 */

case class Countdown(name: String, url:String, eventDate: DateTime, tags:List[String])

//no url/id yet - trying to keep type safety instead of setting to -1 or something
case class AspiringCountdown(name:String, eventDate:DateTime, tags:List[String])

//temp workaround
case class MillisCountdown(name: String, eventDate: Long, tags:List[String], url: String)

object MillisCountdown {
  def apply(countdown: Countdown):MillisCountdown = MillisCountdown(countdown.name, countdown.eventDate.getMillis, countdown.tags, countdown.url)
}