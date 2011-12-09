package co.za.countdown.counter

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.conversions.scala._
import org.bson.types.ObjectId
import co.za.countdown.{AspiringCountdown, Countdown}
import scala.None
import org.joda.time.{DateTime, LocalDate}
import org.joda.time.DateTime

object CountdownService {
  RegisterJodaTimeConversionHelpers()

  val mongo = MongoConnection("nicokruger-rt-l")
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

  def search(name: Option[String], end: Option[Long], tags: List[String], start: Option[Long] = Option(new DateTime().getMillis)): List[Countdown] = {
    val tagTuples = tags.map(tagsField -> _).toList
    val nameTuple = name.map( s => {
      val queryString = s.split("[\\s\\,]").foldLeft(new StringBuilder("(?i).*"))( (builder:StringBuilder, b:String) => builder.append(".*").append(b) )
      nameField -> (queryString.r)
    }).toList

    val orTuples = (tagTuples ++ nameTuple)

    val q: DBObject = if (orTuples.isEmpty) MongoDBObject() else $or(orTuples: _*)
    val results = coll.find(q) map countdownFromDB

    results.filter(c => {
      (start.isEmpty || (c.eventDate.compareTo(new DateTime(start.get)) >= 0)) &&
        (end.isEmpty || (c.eventDate.compareTo(new DateTime(end.get)) <= 0))
    }).toList
  }

  def week: List[Countdown] = {
    val then = new DateTime().plusWeeks(1).getMillis
    search(None, Some(then), List())
  }
  
  def day: List[Countdown] = {
    val then = new DateTime().plusDays(1).getMillis
    search(None, Some(then), List())
  }
  
  def month: List[Countdown] = {
    val then = new DateTime().plusMonths(1).getMillis
    search(None, Some(then), List())
  }

  def retrieveAll: (Int, List[Countdown]) = (coll.size, coll.map(countdownFromDB(_)).toList)

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
