package org.letgo.assignments.letshout

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import org.slf4j.{Logger, LoggerFactory}
import akka.http.scaladsl.server.Directives._
import org.letgo.assignments.letshout.entities.ShoutRequest
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.ask
import com.danielasfregola.twitter4s.TwitterRestClient

object Solution extends scala.App {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  // Read global values from the configuration file
  // I use typesafe config objects because I am used to them :)
  // Reference -> https://github.com/lightbend/config
  // TODO: For the development phase I will use a plane String for the configuration globals
  // Later I will uncoment the line below to pick up the config from a file
  //val config = ConfigFactory.parseFile(new File("path/to/config"))
  val config = ConfigFactory.parseString(
    """
      |version1 : {
      |  server.port = 12000
      |  server.akka.as-name = "letShout"
      |  server.akka.interface = "httpFace"
      |  server.akka.backend = "twitterFace"
      |  twitter.consumer.key = "70xpnqEQvH8SCkJ207dRYfqaB"
      |  twitter.consumer.secret = "QOsrE7r0ArQBUMMa5J0r7FRM8K5gfbQR61uqyH5Ncbn6b0Am6U"
      |  twitter.access.key = ""
      |  twitter.access.secret = ""
      |}
    """.stripMargin
  ).getConfig("version1")
  // Needs to be implicit so it gets picked up by the actor materializer
  implicit val as = ActorSystem(config.getString("server.akka.as-name"))
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = as.dispatcher

  // The twitter requester actor instantiates a twitter rest client and handles requests and responses
  val twitterRequester =
    as.actorOf(
      Props(
        classOf[TwitterActor],
        ConsumerToken(
          config.getString("twitter.consumer.key"),
          config.getString("twitter.consumer.secret")
        ),
        AccessToken(
          config.getString("twitter.access.key"),
          config.getString("twitter.access.secret")
        )
      ),
      config.getString("server.akka.backend")
    )

  logger.debug("Twitter REST Client succesfully started, binding HTTP Server")

  val client = TwitterRestClient.withActorSystem(ConsumerToken(
    config.getString("twitter.consumer.key"),
    config.getString("twitter.consumer.secret")
  ), AccessToken(
    config.getString("twitter.access.key"),
    config.getString("twitter.access.secret")
  ))(implicitly[ActorSystem])

  // Define the endpoints and functionality of out API in a Route object
  val route =
    path("letshout") {
      get {
        parameter("user", "n".as[Int]) { (user, n) =>
          //val shoutedTweets = twitterRequester ? ShoutRequest(user, n)
          val shoutedTweets =
            client.userTimelineForUser( screen_name = user, count = n).map(_.data.map(_.text.toUpperCase + "!").mkString(""))
          complete(shoutedTweets)
        }
      }
    }
}
