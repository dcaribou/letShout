package org.letgo.assignments.letshout

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
case class ShoutedTimeline( user : String, tweets : Seq[String])
case class Tweets(tweets : Seq[String])

// collect your json format instances into a support trait:
object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val toJsonArray = jsonFormat2(ShoutedTimeline)
  implicit val fromJsonArray = jsonFormat1(Tweets)
}
