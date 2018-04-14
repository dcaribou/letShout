package org.letgo.assignments.letshout

import org.letgo.assignments.letshout.exceptions._
import twitter4j.{Paging, Twitter, TwitterException, TwitterFactory}
import twitter4j.auth.AccessToken
import java.nio.file.{Files, Paths}

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

object Twitter4JWrapper {
  // Read access token from local file
  def retrieveAccessToken(): Option[AccessToken] = {
    val config = ConfigFactory.load()
    try {
      Some(new AccessToken(config.getString("twitter.access.key"), config.getString("twitter.access.secret")))
    }
    catch {
      case e : com.typesafe.config.ConfigException.Missing =>
        println(s"${e.getMessage}")
        None
      case e : java.nio.file.NoSuchFileException =>
        println("An access token needs to be obtained for the application prior to start the server.")
        println("Please follow the procedure in README.md to obtain such a key.")
        None
    }
  }
  // Create an authenticated Twitter4JClient
  def apply() : Twitter4JWrapper = {
    val config = ConfigFactory.load()
    retrieveAccessToken() match {
      case Some(accessToken) => {
        val twitter = TwitterFactory.getSingleton()
        twitter.setOAuthConsumer(
          config.getString("twitter.consumer.key"),
          config.getString("twitter.consumer.secret")
        )
        twitter.setOAuthAccessToken(accessToken)
        new Twitter4JWrapper(twitter)
      }
      case None => throw ClientAuthenticationException("Could not obtain access token")
    }
  }
}

// Wrapper of the Twitter4J client
class Twitter4JWrapper(authenticatedTwitterClient: Twitter) extends PluggableShouter {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  import collection.JavaConverters._
  def getShoutedTweets(screenName : String, count : Int) : Seq[String] = {
    try {
      authenticatedTwitterClient.getUserTimeline(
        screenName,
        new Paging(1, count)
      ).iterator().asScala.toSeq.map(status => s"${status.getText.toUpperCase}!")
    }
    catch {
      case e: TwitterException if e.getErrorCode == 34 =>
        logger.error("The page does not exist. Have you introduced a valid user?")
        Seq("We could not find your page!")
      case e : TwitterException =>
        logger.error(s"Error querying twitter: ${e.getMessage}")
        Seq("We are facing some problems now back in the server. Hold on a sec.")
    }
  }

}
