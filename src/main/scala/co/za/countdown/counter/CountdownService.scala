package co.za.countdown.counter

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.conversions.scala._
import co.za.countdown.Countdown._
import org.bson.types.ObjectId
import co.za.countdown.{AspiringCountdown, Countdown}
import util.matching.Regex
import org.joda.time.{DateTimeZone, DateTime, LocalDate}

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
  val tagsField = "tags"

  val defaultName = "Unnamed"

  def upsertCountdown(countdown: AspiringCountdown) = {
    coll.update(MongoDBObject(nameField -> countdown.name),
      dbObjFromAspiring(countdown), true, false)
  }

  def insertCountdown(countdown: AspiringCountdown): Option[Countdown] = {
    coll += dbObjFromAspiring(countdown)
    searchBy(countdown).toList.headOption
  }

  def searchBy(countdown: AspiringCountdown) = {
    coll.find(dbObjFromAspiring(countdown)).map(countdownFromDB)
  }

  def regexify( query: String) : Regex =  {
    query.split("""[\s\,]""")
      .foldLeft(new StringBuilder("(?i).*"))( (builder:StringBuilder, part:String) => builder.append(".*").append(part) )
    .toString.r
  }

  def regexTuples(fieldName:String, values: List[String]) : List[ (String, Regex) ] =
    values.foldLeft(List[(String, Regex)]())((acc: List[(String, Regex)], s: String) => (fieldName, regexify(s)) :: acc)

  def search(name: Option[String], start: Option[Long], end: Option[Long], tags: List[String]) = {

    val tagsQuery : DBObject = if (!tags.isEmpty) $or(regexTuples(tagsField, tags):_*) else MongoDBObject()
    val query = regexTuples(nameField, name.toList).foldLeft(tagsQuery)(_+=_)
    val results = coll.find(query) map countdownFromDB

    results.filter((countdown: Countdown) => {
      (start.isEmpty || (countdown.eventDate.getMillis >= start.get)) &&
        (end.isEmpty || (countdown.eventDate.getMillis <= end.get))
    })
  }

  def retrieveAll: List[Countdown] = {
    (for {dbobj <- coll}
    yield countdownFromDB(dbobj)).toList
  }

  def retrieveByName(name: String): Option[Countdown] = {
    coll.findOne(MongoDBObject(nameField -> name)).map(countdownFromDB)
  }

  def retrieveById(id: ObjectId): Option[Countdown] = {
    coll.findOne(MongoDBObject("_id" -> id)).map(countdownFromDB)
  }


  private def dbObjFromAspiring(countdown: AspiringCountdown) =
    MongoDBObject(nameField -> countdown.name, eventDateField -> countdown.eventDate, tagsField -> countdown.tags)

  private def countdownFromDB(dbobj: DBObject) = Countdown(name(dbobj),
    idAsString(dbobj),
    eventDate(dbobj),
    tags(dbobj))

  private def idAsString(dbobj: DBObject) = dbobj.getAsOrElse[ObjectId]("_id", new ObjectId()).toString

  private def eventDate(dbobj: DBObject) = dbobj.getAsOrElse[DateTime](eventDateField, new DateTime())

  private def name(dbobj: DBObject) = dbobj.getAsOrElse[String](nameField, defaultName)

  private def tags(dbobj: DBObject): List[String] = {
    dbobj.getAsOrElse[BasicDBList](tagsField, MongoDBList()).toList collect {
      case s: String => s
    }
  }
}