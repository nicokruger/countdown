package co.za.countdown.counter

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.conversions.scala._
import co.za.countdown.Countdown._
import co.za.countdown.Countdown
import org.joda.time.{DateTime, LocalDate}

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 7:46 PM
 */

object CountdownService {
  RegisterJodaTimeConversionHelpers()

  val mongo = MongoConnection()
  val coll = mongo("countDownDB")("countdown")

  def upsertCountdown( countdown: Countdown) = {
    coll.update(MongoDBObject("name" -> countdown.name), MongoDBObject("name" -> countdown.name, "date" -> countdown.eventDate),true, false)
  }

  def retrieveAll: Iterable[Countdown] = {
    for { x <- coll}
      yield Countdown(x.getAsOrElse[String]("name", "Unnamed"), x.getAsOrElse[DateTime]("date", new DateTime()))
  }

  def retrieveByName(name:String) : Option[Countdown] = {
     coll.findOne( MongoDBObject("name" -> name) ).map( (dbobj:DBObject) =>
       Countdown(name, dbobj.getAsOrElse[DateTime]("date", new DateTime())))
  }
}
//object CMain extends App{
//  CountdownService.upsertCountdown( ("test item", new DateTime()))
//}