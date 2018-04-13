package org.letgo.assignments.letshout

import org.slf4j.{Logger, LoggerFactory}
import com.danielasfregola.twitter4s.TwitterRestClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Twitter4SWrapper extends PluggableShouter {
  val restClient =  TwitterRestClient()
  def getShoutedTweets(screenName : String, count : Int) : Seq[String] =
    Await.result(
      restClient.userTimelineForUser(screen_name = screenName, count = count)
        .map(_.data.map(_.text.toUpperCase + "!")),
      10 seconds
    )

}
