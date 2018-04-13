package org.letgo.assignments.letshout

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.slf4j.{Logger, LoggerFactory}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import org.apache.log4j.BasicConfigurator

import scala.io.StdIn

object RouteFactory {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  import org.letgo.assignments.letshout.JsonSupport._ // Required for a spray supported json conversion of a Seq[String]
  def singleEnded(endpoint : String, handler : (String, Int) => Seq[String]): Route =
    path(endpoint) {
      get {
        parameter("user", "n".as[Int]) { (user, n) =>
          logger.debug(s"Received a shout request for $n tweets from $user")
          complete(handler(user, n))
        }
      }
    }
  def apply(endpoint : String, handler : (String, Int) => Seq[String]) =
    singleEnded(endpoint, handler)
}

object LetShout extends scala.App {
  BasicConfigurator.configure()
  import Implicits._
  val logger: Logger = LoggerFactory.getLogger(getClass)
  // Read global values from the configuration file
  // I use typesafe config objects because I am used to them :)
  // Reference -> https://github.com/lightbend/config
  val config = ConfigFactory.load()
  // Needs to be implicit so it gets picked up by the actor materializer
  implicit val as = ActorSystem(config.getString("server.akka.as-name"))
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = as.dispatcher
  // The twitter requester actor instantiates a twitter rest client and handles requests and responses
  // For the correct initialization of the twitter4j client, an 'auth' file needs to be available in the
  // classpath, as it is obtained in the procedure for authenticating the application in the twitter api
  val shouter = PluggableShouter()

  // Bind server to an interface and start listening
  val bindingFuture =
    Http().bindAndHandle(
      "letshout" ~> ((user : String, n : Int) => shouter.getShoutedTweets(user, n)),
      "localhost",
      config.getInt("server.port")
    )
  logger.info(s"Http server listening on port ${config.getInt("server.port")}")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => as.terminate()) // and shutdown when done
}
