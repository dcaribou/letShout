package org.letgo.assignments.letshout

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route

/**
  * Implicit conversions
  */
object Implicits {

  /**
    * This implicit class enables an idiomatic way of connecting a String to a PluggableSource to create an Akka Route.
    * @param endpoint The label of the endpoint
    */
  implicit class ConnectableString(endpoint : String){
    /**
      * Convert a String to an Akka Route that we can bind on and start listening for requests, by connecting a [[String]]
      * endpoint to handler funtion for the requests
      * @param handler The handler function
      * @param system A reference to the Akka [[ActorSystem]]
      * @return A [[Route]]
      */
    def ~>(handler : (String, Int) => Seq[String])(implicit system : ActorSystem): Route =
      RouteFactory(endpoint, handler)(system)
  }
}
