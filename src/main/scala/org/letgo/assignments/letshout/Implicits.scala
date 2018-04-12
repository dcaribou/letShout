package org.letgo.assignments.letshout

import akka.http.scaladsl.server.Route

object Implicits {
  implicit class ConnectableString(endpoint : String){
    def ~>(handler : (String, Int) => Seq[String]): Route =
      RouteFactory.singleEnded(endpoint, handler)
  }
}
