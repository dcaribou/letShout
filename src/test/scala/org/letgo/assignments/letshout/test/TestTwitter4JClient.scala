package org.letgo.assignments.letshout.test

import org.letgo.assignments.letshout.GetShout
import org.scalatest.{FlatSpec, Matchers}

class TestTwitter4JClient extends FlatSpec with Matchers {
  org.apache.log4j.BasicConfigurator.configure()
  val twitterClient =
    GetShout(
      "70xpnqEQvH8SCkJ207dRYfqaB",
      "QOsrE7r0ArQBUMMa5J0r7FRM8K5gfbQR61uqyH5Ncbn6b0Am6U"
    )

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
