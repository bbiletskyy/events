package events.main

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import akka.actor.ActorRef
import spray.http.MediaTypes.{ `text/html` }
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller

/**Actor Service that  */
class RestServiceActor(connector: ActorRef) extends Actor with RestService {
  def actorRefFactory = context
  def receive = runRoute(route)

  def communicate(e: Event) = connector ! e
  override def preStart() = println(s"Starting rest-service actor at ${context.self.path}")
}

/** This trait defines the routing */
trait RestService extends HttpService {
  def communicate(e: Event)

  import EventsJsonProtocol._
  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Events REST API</h1>
                <a href="/events">Examples</a>
                of event json format.
              </body>
            </html>
          }
        }
      }
    } ~ path("event") {
      post {
        entity(as[Event]) { event =>
          communicate(event)
          complete(event)
        }
      }
    } ~ path("events") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            Seq(RandomEvent(), RandomEvent(), RandomEvent())
          }
        }
      }
    }
}