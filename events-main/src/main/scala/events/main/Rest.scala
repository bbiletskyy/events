package events.main
import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.actor.ActorRef

/** REST api init routines */
object Rest {
  def init(actorSystem: ActorSystem, driverHost: String, driverPort: Int, receiverActorName: String) {
    implicit val system = actorSystem
    val connector = lookup(actorSystem, driverHost, driverPort, receiverActorName)
    val service = system.actorOf(Props(new RestServiceActor(connector)), "rest-service")

    implicit val timeout = Timeout(5.seconds)
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }

  def lookup(actorSystem: ActorSystem, driverHost: String, driverPort: Int, actorName: String): ActorRef = {
    import scala.concurrent.duration._
    val url = s"akka.tcp://sparkDriver@$driverHost:$driverPort/user/Supervisor0/$actorName"
    val timeout = 10.seconds
    val receiver = Await.result(actorSystem.actorSelection(url).resolveOne(timeout), timeout)
    receiver
  }
}