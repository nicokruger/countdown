package co.za.countdown.counter

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.conversions.scala._
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

  def upsertCountdown( countdown: (String, DateTime)) = {
    coll.update(MongoDBObject("name" -> countdown._1), MongoDBObject("name" -> countdown._1, "date" -> countdown._2),true, false)
  }

  def retrieveAll: Iterable[(String,  LocalDate)] = {
    for { x <- coll}
      yield (x.getAsOrElse[String]("name", "Unnamed"), x.getAsOrElse[LocalDate]("date", new LocalDate()))
  }

  def retrieveByName(name:String) : Option[(String,  LocalDate)] = {
     coll.findOne( MongoDBObject("name" -> name) ).map( (dbobj:DBObject) =>
       (name, dbobj.getAsOrElse[LocalDate]("date", new LocalDate())))
  }
}
//object CMain extends App{
//  CountdownService.upsertCountdown( ("test item", new DateTime()))
//}