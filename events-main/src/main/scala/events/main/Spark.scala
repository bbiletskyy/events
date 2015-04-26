package events.main

import akka.actor.Actor.Receive
import akka.actor.{ Actor, ActorIdentity, Identify, Props }
import java.util.concurrent.Executors
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.storage._
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver._
import scala.concurrent.Await
import org.apache.spark.streaming.StreamingContext._

/** This object is in charge of Spark initialisation*/
object Spark {
  def init(driverHost: String, driverPort: Int, receiverActorName: String) = {
    val conf = sparkConf(driverHost, driverPort)
    val ssc = new StreamingContext(conf, Seconds(1))
    val actorStream = ssc.actorStream[Event](Props[Receiver], receiverActorName)

    val filteredCategoryGuidPairs = actorStream.filter(event => event.category != "info").map(event => (event.category, event.guid))
    val reducedCategoryGuids = filteredCategoryGuidPairs.reduceByKey((a, b) => a + ", " + b)

    reducedCategoryGuids.foreachRDD(rdd => rdd.foreach(pair => println(pair._1 + ": " + pair._2)))

    ssc.start()
    ssc.awaitTermination(10000)
    SparkEnv.get.actorSystem
  }

  def sparkConf(driverHost: String, driverPort: Int) = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("Spark Streaming with Scala and Akka")
    .set("spark.logConf", "true")
    .set("spark.driver.port", driverPort.toString)
    .set("spark.driver.host", driverHost)
    .set("spark.akka.logLifecycleEvents", "true")
}

/** This actor is a bridge to Spark. It is in charge of receiving data */
class Receiver extends Actor with ActorHelper {
  override def preStart() = {
    println(s"Starting Receiver actor at ${context.self.path}")
  }
  def receive = {
    case e: Event =>
      store(e)
  }
}
