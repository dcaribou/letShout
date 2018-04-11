package org.letgo.assignments.letshout

import org.letgo.assignments.letshout.exceptions._
import twitter4j.{Paging, Twitter, TwitterException, TwitterFactory}
import java.nio.file.{Files, Paths}

import org.slf4j.{Logger, LoggerFactory}
import twitter4j.auth.AccessToken
object GetShout {
  // Read access token from local file
  def retrieveAccessToken(): Option[AccessToken] = {
    try {
      val rawSecurity = Files.readAllLines(Paths.get("auth"))
      Some(new AccessToken(rawSecurity.get(0), rawSecurity.get(1)))
    }
    catch{
      case e : java.nio.file.NoSuchFileException =>
        println("An access token needs to be obtained for the application prior to start the server.")
        println("Please follow the procedure in README.md to obtain such a key.")
        None
    }
  }
  // Create an authenticated Twitter4JClient
  def apply(consumerKey : String, consumerSecret : String) : GetShout = {
    retrieveAccessToken() match {
      case Some(accessToken) => {
        val twitter = TwitterFactory.getSingleton()
        twitter.setOAuthConsumer(consumerKey, consumerSecret)
        twitter.setOAuthAccessToken(accessToken)
        new GetShout(twitter)
      }
      case None => throw ClientAuthenticationException("Could not obtain access token")
    }
  }
}

class GetShout(authenticatedTwitterClient: Twitter) {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  import collection.JavaConverters._
  def getShoutedTweets(screenName : String, count : Int) : Seq[String] =
    authenticatedTwitterClient.getUserTimeline(
      screenName,
      new Paging(1, count)
    ).iterator().asScala.toSeq.map(status => s"${status.getText.toUpperCase}!")
}
