package events.main

import spray.json.DefaultJsonProtocol
import scala.util.Random
import java.util.Date

/**Event model */
case class Event(guid: String, category: String, action: String, timestamp: String)

/**Json marshalling/unmarshalling */
object EventsJsonProtocol extends DefaultJsonProtocol {
  implicit val EventFormat = jsonFormat4(Event)
}

/**Random event generation routines */
object RandomEvent {
  def apply() = Event(randomGiud, randomCategory, randomAction, timestamp)
  def randomGiud = java.util.UUID.randomUUID.toString
  def timestamp = new Date().toString()
  def randomCategory = categories(random.nextInt(categories.size))
  def randomAction = actions(random.nextInt(actions.size))
  val random = new Random
  val categories = Seq("info", "warning", "error")
  val actions = Seq("POST /event", "GET /events", "GET /")
}