package org.letgo.assignments.letshout

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

// reference: https://doc.akka.io/docs/akka-http/current/common/json-support.html
object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol