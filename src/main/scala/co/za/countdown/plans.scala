package co.za.countdown

import counter.CountdownService
import serve.Countdowns
import unfiltered.filter.Plan
import unfiltered.request._
import net.liftweb.json.Serialization.write
import unfiltered.response._
import net.liftweb.json.{NoTypeHints, Serialization}
import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}
import com.gargoylesoftware.htmlunit.html.HtmlPage

object Transform {
  implicit val formats = Serialization.formats(NoTypeHints)

  def asHtml(l: List[Countdown]) = {
    def transform(ll: List[Countdown]) = {
      val webClient = new WebClient(BrowserVersion.FIREFOX_3_6)
      val page: HtmlPage = webClient.getPage(Thread.currentThread.getContextClassLoader.getResource("static/index.html"))

      page.executeJavaScript("var m = model($(\"#countdownlist\"));\n")
      ll.foreach(e => {
        val converted = new MillisCountdown(e.name, e.eventDate, e.tags, e.url)
        page.executeJavaScript("m.putCountdown(" + write(converted) + ");")
      })
      page.asXml
    }
    HtmlContent ~> ResponseString(transform(l))
  }

  def asJson(l: List[Countdown]) = asJsonT(l.map(c => new MillisCountdown(c.name, c.eventDate, c.tags, c.url)))
//    JsonContent ~> ResponseString(write(Countdowns())))

  def asJsonT(l: List[MillisCountdown]) = JsonContent ~> ResponseString(write(Countdowns(l)))
}

class Lookup extends Plan {
  import Transform._

  def intent = {
    case req @ GET(Path(Seg(Nil))) => {
      val Params(countdown) = req
      val id = countdown.headOption.map(_._1).getOrElse("")

      // Something more idiomatic?
      if (id == "") {
        Ok ~> displayWeek
      } else {
        ResponseString("id: " + id)
      }
    }

    case GET(Path(Seg("day" :: Nil)) & Accepts.Html(_)) => asHtml(CountdownService.day)
    case GET(Path(Seg("week" :: Nil))& Accepts.Html(_)) => asHtml(CountdownService.week)
    case GET(Path(Seg("month" :: Nil))& Accepts.Html(_)) => asHtml(CountdownService.month)
    case GET(Path(Seg("year" :: Nil))& Accepts.Html(_)) => ResponseString("/year")

    case GET(Path(Seg("day" :: Nil)) & Accepts.Json(_)) => asJson(CountdownService.day)
    case GET(Path(Seg("week" :: Nil))& Accepts.Json(_)) => asJson(CountdownService.week)
    case GET(Path(Seg("month" :: Nil))& Accepts.Json(_)) => asJson(CountdownService.month)
    case GET(Path(Seg("year" :: Nil))& Accepts.Json(_)) => ResponseString("/year")

    case GET(Path(Seg("random" :: Nil)) & Accepts.Html(_)) => asHtml(random)
    case GET(Path(Seg("random" :: Nil)) & Accepts.Json(_)) => asJson(random)
  }

  def displayWeek = ResponseString("/week")

  // Clean up this method
  def random: List[Countdown] = {
    val (size, data) = CountdownService.retrieveAll

    if (size > 0) {
      List(data((math.random * (size - 1)).toInt))
    }
    else {
      List()
    }
  }
}

class Search extends Plan {
  import co.za.countdown.Transform._

  def intent = {
    case c @ GET(Path(Seg("countdowns" :: Nil)) & Accepts.Json(_)) => {
      val Params(p) = c
      asJsonT(searchResponse(p))
    }

    case GET(Path(Seg("tags" :: Nil)) & Accepts.Json(_)) => ResponseString("coundotnes")
  }

  def searchResponse(params: Map[String, Seq[String]]) = {
    val searchResultMap = (countdown:Countdown) => MillisCountdown(countdown)

    // Getting the params must be done cleaner!!!!
    val name = params.getOrElse("name", Nil).headOption
    val startMillis = params.getOrElse("start", Nil).map(_.toLong).headOption
    val endMillis = params.getOrElse("end", Nil).map(_.toLong).headOption
    val tags = params.getOrElse("tags", Nil).flatMap(_.split(",")).toList

    println(name)
    println(startMillis)
    println(endMillis)
    println(tags)
    CountdownService.search(name, endMillis, tags, startMillis).map(searchResultMap)
  }
}

class Create extends Plan {
  def intent = {
    case POST(Path(Seg("countdown" :: "new" :: Nil)) & Accepts.Json(_)) => ResponseString("asdsd")
    case POST(Path(Seg("countdown" :: "upsert" :: Nil)) & Accepts.Json(_)) => ResponseString("upsert still todo")
  }
}