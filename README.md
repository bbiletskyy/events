Events - Microservice that analizes streams of events obtained via the rest interface by applying map/reduce in real time.

Project consists of 2 parts:
1. events-main - microservice application built with Spay, Akka Actors, Spark
2. events-test - load-testing application, sending events to the microservice, built using Gatling

How to run:
1. sbt events-main/run - to run microservice, navigate to http://localhost:8080, or POST event in application/json format to http://localhost:8080/event
2. sbt events-test/gatling:test - to run load test
