package co.za.countdown.main

import co.za.countdown.serve.ServeCountdowns
import java.net.URL
import unfiltered.jetty.ContextBuilder

/**
 * User: dawid
 * Date: 11/29/11
 * Time: 8:44 PM
 */

object TestStartup extends App {
  unfiltered.jetty.Http(55555)
    .context("/static") {
      (builder: ContextBuilder) => builder.resources(new URL(getClass.getResource("/static/"), "."))}
    .filter(ServeCountdowns.countdowns).run()
}