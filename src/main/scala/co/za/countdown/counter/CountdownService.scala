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

  val eventDateField = "eventDate"
  val nameField = "name"

  val defaultName = "Unnamed"

  def upsertCountdown( countdown: (String, DateTime)) = {
    coll.update(MongoDBObject(nameField -> countdown._1), MongoDBObject(nameField -> countdown._1, eventDateField -> countdown._2),true, false)
  }

  def retrieveAll: Iterable[Countdown] = {
    for { dbobj <- coll}
      yield Countdown(name(dbobj),
      idAsString(dbobj),
      eventDate(dbobj))
  }

  def retrieveByName(name:String) : Option[Countdown] = {
     coll.findOne( MongoDBObject(nameField -> name) ).map( (dbobj:DBObject) =>
       Countdown(name,
         idAsString(dbobj),
         eventDate(dbobj)))
  }

  def retrieveById(id:ObjectId) : Option[Countdown] = {
     coll.findOne( MongoDBObject("_id" -> id) ).map( (dbobj:DBObject) =>
       Countdown(name(dbobj),
         idAsString(dbobj),
         eventDate(dbobj)))
  }

  private def idAsString(dbobj:DBObject) = dbobj.getAsOrElse[ObjectId]("_id", new ObjectId()).toString
  private def eventDate(dbobj: DBObject) = dbobj.getAsOrElse[DateTime](eventDateField, new DateTime())
  private def name(dbobj: DBObject) = dbobj.getAsOrElse[String](nameField, defaultName)
}
//object CMain extends App{
//  CountdownService.upsertCountdown( ("test item", new DateTime()))
//}