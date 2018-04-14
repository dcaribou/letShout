package org.letgo.assignments.letshout

import org.slf4j.{Logger, LoggerFactory}
import com.danielasfregola.twitter4s.TwitterRestClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.danielasfregola.twitter4s.exceptions.TwitterException

// Wrapper for te Twitter4S client is much simpler
object Twitter4SWrapper extends PluggableShouter {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val restClient =  TwitterRestClient()
  def getShoutedTweets(screenName : String, count : Int) : Seq[String] = {
    try {
      Await.result( // TODO: This blocking call really needs to be gone as soon as I have time
        restClient.userTimelineForUser(screen_name = screenName, count = count)
          .map(_.data.map(_.text.toUpperCase + "!")),
        10 seconds
      )
    }
    catch {
      case e : TwitterException if e.code.intValue == 404 =>
        logger.error("The page does not exist. Have you introduced a valid user?")
        Seq("We could not find your page!")
      case e : TwitterException =>
        logger.error(s"Error querying twitter: ${e.getMessage}")
        Seq("We are facing some problems now back in the server. Hold on a sec.")
    }
  }




}
