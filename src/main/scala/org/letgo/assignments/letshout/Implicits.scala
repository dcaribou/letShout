package org.letgo.assignments.letshout

import akka.actor.ActorSystem
import akka.http.caching.scaladsl.Cache
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.{Route, RouteResult}

object Implicits {
  implicit class ConnectableString(endpoint : String){
    def ~>(handler : (String, Int) => Seq[String])(implicit system : ActorSystem): Route =
      RouteFactory(endpoint, handler)(system)
  }
}
