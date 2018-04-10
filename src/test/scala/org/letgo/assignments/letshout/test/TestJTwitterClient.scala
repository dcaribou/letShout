package org.letgo.assignments.letshout.test

import org.letgo.assignments.letshout.GetShout
import org.scalatest.{FlatSpec, Matchers}

class TestJTwitterClient extends FlatSpec with Matchers {
  "A client" should "get statuses from a given account" in {
    val myClient = GetShout(
      "70xpnqEQvH8SCkJ207dRYfqaB",
      "QOsrE7r0ArQBUMMa5J0r7FRM8K5gfbQR61uqyH5Ncbn6b0Am6U"
    )
    val tweets = myClient.getShoutedTweets("JUc3m", 5)
    tweets.foreach(println(_))
  }

}
