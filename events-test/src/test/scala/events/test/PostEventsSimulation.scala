package events.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.http.config.HttpProtocolBuilder.toHttpProtocol
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder
import spray.json._
import events.main.Event
import events.main.RandomEvent
import events.main.EventsJsonProtocol._

/** Posts 1000 random events with 10 concurrent users */
class PostEventSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val eventFeeder = Iterator.continually(Map("event" -> RandomEvent().toJson.compactPrint))
  val scn = scenario("Posting Random Events").repeat(1000) {
    feed(eventFeeder).
      exec(http("Posting a a random event")
        .post("/event").body(StringBody("${event}")).asJSON)
      .pause(5 millis)
  }

  setUp(scn.inject(atOnceUsers(10))).protocols(httpConf)
}
