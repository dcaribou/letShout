package org.letgo.assignments.letshout.test

import org.scalatest.{FlatSpec, Matchers}
import com.danielasfregola.twitter4s.entities.{RatedData, Tweet}
import org.letgo.assignments.letshout.Twitter4SWrapper

class TestTwitter4SWrapper extends FlatSpec with Matchers {
  val restClient = Twitter4SWrapper
  "Twitter4s" should "get N statuses from a given account that has at least N statuses" in {
    restClient.getShoutedTweets(screenName = "dcaramu", count = 3) shouldEqual Seq(
      "THIRD TWEET!", "SECOND TWEET!", "FIRST TWEET!"
    )
  }
}