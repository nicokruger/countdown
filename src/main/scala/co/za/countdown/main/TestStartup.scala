package co.za.countdown.main

import java.net.URL
import unfiltered.jetty.ContextBuilder
import java.io.File
import co.za.countdown._
import unfiltered.jetty.Server

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:44 PM
 */

object TestStartup {
//  unfiltered.jetty.Http(55555)
//    .context("/") {
//      (builder: ContextBuilder) => builder.filter(ServeCountdowns.countdowns); builder.resources(new URL(getClass.getResource("/static/"), "."))
//    }
//    .context("/filesystem") {
//      (builder: ContextBuilder) => builder.resources(new URL(new File("src/main/resources/static").toURI.toURL, "."))
//    }.run()
/*    .context("/") {
      (builder: ContextBuilder) => builder.resources(new URL(getClass.getResource("/static/"), "."))}
    .context("/filesystem") {
      (builder: ContextBuilder) => builder.resources(new URL(new File("src/main/resources/static").toURI.toURL, "."))}*/
  
//  val plans = Seq(new Lookup)

  val plans = Seq(new Lookup, new Create, new Search)
  def applyPlans = plans.foldLeft(_: Server)(_ filter _)

  def main(args: Array[String]) {
    applyPlans(unfiltered.jetty.Http(55555)).resources(getClass.getResource("/static/")).run()
  }
}
