package co.za.countdown.source

import org.specs2.mutable._
import scalax.io._
import xml.XML
import java.io.InputStream
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.LocalDate

/**
 * User: dawidmalan
 * Date: 2011/11/29
 * Time: 1:29 PM
 */

class GamesWebScrapeTest extends Specification {
  "The scraper" should {
    "scrape some dates for me from the bt games release date page" in {
      val xmlStream:InputStream = getClass.getResourceAsStream("/co/za/countdown/bt_release_dates.html")
      val results = GamesWebScrape.getDateItems(xmlStream)
      println(results)
      results.length mustEqual  20
    }
  }
}