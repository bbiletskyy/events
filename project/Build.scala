import sbt._
import Keys._
import io.gatling.sbt.GatlingPlugin

object EventsRootBuild extends Build {
    lazy val root = Project(id = "events-root",
                            base = file(".")).aggregate(main, client)

    lazy val main = Project(id = "events-main",
                           base = file("events-main")).
                           settings(libraryDependencies ++= Dependencies.sparySparkAkkaHadoop)


    lazy val client = Project(id = "events-test",
                           base = file("events-test")).
                           dependsOn(main).enablePlugins(GatlingPlugin).
                           settings(libraryDependencies ++= Dependencies.gatling)

 
}

