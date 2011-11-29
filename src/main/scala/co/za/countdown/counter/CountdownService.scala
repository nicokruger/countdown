package co.za.countdown.counter

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.conversions.scala._
import co.za.countdown.Countdown._
import co.za.countdown.Countdown
import org.joda.time.{DateTime, LocalDate}
import org.bson.types.ObjectId

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 7:46 PM
 */

object CountdownService {
  RegisterJodaTimeConversionHelpers()

  val mongo = MongoConnection()
  val coll = mongo("countDownDB")("countdown")

  def upsertCountdown( countdown: (String, DateTime)) = {
    coll.update(MongoDBObject("name" -> countdown._1), MongoDBObject("name" -> countdown._1, "date" -> countdown._2),true, false)
  }

  def retrieveAll: Iterable[Countdown] = {
    for { x <- coll}
      yield Countdown(x.getAsOrElse[String]("name", "Unnamed"),
      x.getAsOrElse[ObjectId]("_id", new ObjectId()).toString,
      x.getAsOrElse[DateTime]("eventDate", new DateTime()))
  }

  def retrieveByName(name:String) : Option[Countdown] = {
     coll.findOne( MongoDBObject("name" -> name) ).map( (dbobj:DBObject) =>
       Countdown(name,
         dbobj.getAsOrElse[ObjectId]("_id", new ObjectId()).toString,
         dbobj.getAsOrElse[DateTime]("date", new DateTime())))
  }

  def retrieveById(id:ObjectId) : Option[Countdown] = {
     coll.findOne( MongoDBObject("_id" -> id) ).map( (dbobj:DBObject) =>
       Countdown(dbobj.getAsOrElse[String]("name", "Unnamed"),
         dbobj.getAsOrElse[ObjectId]("_id", new ObjectId()).toString,
         dbobj.getAsOrElse[DateTime]("date", new DateTime())))
  }
}
//object CMain extends App{
//  CountdownService.upsertCountdown( ("test item", new DateTime()))
//}