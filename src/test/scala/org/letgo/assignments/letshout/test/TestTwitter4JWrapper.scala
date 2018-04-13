package org.letgo.assignments.letshout.test

import org.letgo.assignments.letshout.Twitter4JWrapper
import org.scalatest.{FlatSpec, Matchers}

class TestTwitter4JWrapper extends FlatSpec with Matchers {
  val twitterClient = Twitter4JWrapper()
  "GetShout" should "get N statuses from a given account that has at least N statuses" in {
    twitterClient.getShoutedTweets("dcaramu", 3) shouldEqual(
      Seq("THIRD TWEET!", "SECOND TWEET!", "FIRST TWEET!")
    )
  }
  "GetShout" should "get the maximum number of stasuses from an account when N is higher than the published statuses" in {
    twitterClient.getShoutedTweets("dcaramu", 100) shouldEqual(
      Seq("THIRD TWEET!", "SECOND TWEET!", "FIRST TWEET!")
    )
  }
}
