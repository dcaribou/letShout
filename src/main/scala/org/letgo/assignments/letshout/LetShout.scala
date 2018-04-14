package org.letgo.assignments.letshout

// config objects factory
import com.typesafe.config.ConfigFactory
// akka imports
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.http.caching.scaladsl.Cache
import akka.http.caching.LfuCache
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.directives.CachingDirectives._
// logging devices
import org.slf4j.{Logger, LoggerFactory}
import org.apache.log4j.BasicConfigurator

import scala.io.StdIn

/**
  * Factory for Akka [[Route]]s.
  */
object RouteFactory {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val keyer: PartialFunction[RequestContext, Uri] = {case r : RequestContext => r.request.uri} // For caching
  import org.letgo.assignments.letshout.JsonSupport._ // Required for a spray supported json conversion of a Seq[String]
  /**
    * Creates a [[Route]] with a single endpoit whose requests are handled by a generic function.
    * @param endpoint The name (label) of the endpoit. This is the path in which it's reachable in the REST
    * @param handler A generic function that takes two parameters, [[String]] and [[Int]] and returns a [[Seq]] of
    *                [[String]]s
    * @param lfu A cacher. Reference: https://doc.akka.io/docs/akka-http/current/common/caching.html
    * @return a [[Route]]
    */
  def singleEnded(endpoint : String, handler : (String, Int) => Seq[String])(implicit lfu : Cache[Uri, RouteResult]): Route =
    path(endpoint) {
      get {
        cache(lfu, keyer) {
          parameter("user", "n".as[Int]) { (user, n) =>
            logger.debug(s"Received a shout request for $n tweets from $user")
            complete(handler(user, n))
          }
        }
      }
    }
  // This is just a nice wrapper from the method above
  def apply(endpoint : String, handler : (String, Int) => Seq[String])(implicit system : ActorSystem) = {
    implicit val lfuCache : Cache[Uri, RouteResult] = LfuCache[Uri, RouteResult]
    singleEnded(endpoint, handler)
  }
}

/**
  * Main class/objext
  */
object LetShout extends scala.App {
  BasicConfigurator.configure() // Load logging configurations
  import Implicits._
  val logger: Logger = LoggerFactory.getLogger(getClass)
  // Read global values from the configuration file I use typesafe config objects because I am used to them :)
  // Reference -> https://github.com/lightbend/config
  val config = ConfigFactory.load()
  // Needs to be implicit so it gets picked up by the actor materializer
  implicit val as = ActorSystem(config.getString("server.akka.as-name"))
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = as.dispatcher
  // The twitter requester (shouter) instantiates a twitter rest client and handles requests and responses
  // Default shouter is defined in the application.conf file
  val shouter = PluggableShouter()
  // Bind server to an interface and start listening
  val bindingFuture =
    Http().bindAndHandle(
      config.getString("server.endpoint") ~> shouter.getShoutedTweets, // Connect a endpoint to a shouter to create a Route
      config.getString("server.interface"),
      config.getInt("server.port")
    )
  logger.info(s"Http server listening on port ${config.getInt("server.port")}")
  // Wait for user input for a nice shutdown
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => as.terminate()) // and shutdown when done
}
