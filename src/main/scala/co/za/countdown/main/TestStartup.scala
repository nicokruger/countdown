package co.za.countdown.main

import co.za.countdown.serve.ServeCountdowns
import java.net.URL
import unfiltered.jetty.ContextBuilder
import scalax.io.Resource
import java.io.File

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:44 PM
 */

object TestStartup extends App {
  unfiltered.jetty.Http(55555)
    .context("/") {
      (builder: ContextBuilder) => builder.filter(ServeCountdowns.countdowns); builder.resources(new URL(getClass.getResource("/static/"), "."))
    }
    .context("/filesystem") {
      (builder: ContextBuilder) => builder.resources(new URL(new File("src/main/resources/static").toURI.toURL, "."))
    }.run()
/*    .context("/") {
      (builder: ContextBuilder) => builder.resources(new URL(getClass.getResource("/static/"), "."))}
    .context("/filesystem") {
      (builder: ContextBuilder) => builder.resources(new URL(new File("src/main/resources/static").toURI.toURL, "."))}*/
}
