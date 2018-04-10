package org.letgo.assignments.letshout.test

import scala.concurrent.{ ExecutionContext, Future, Promise, Await }
import scala.concurrent.duration._

import akka.actor.ActorSystem
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import org.scalatest._
import scala.util.{Success, Failure}


class TestTwitterClient extends FlatSpec with Matchers {
  implicit val as = ActorSystem()
  implicit val executionContext = as.dispatcher
  "A client" should "get statuses from a given account" in {
    val client = TwitterRestClient.withActorSystem(ConsumerToken(
      "70xpnqEQvH8SCkJ207dRYfqaB",
      "QOsrE7r0ArQBUMMa5J0r7FRM8K5gfbQR61uqyH5Ncbn6b0Am6U"
    ), AccessToken(
      //"2994173326-TVPbnhRCcPCt6aQhnGcya5aCGZf0hZ4hPA9CQ4D",
      "",
      //"emd0VEhMzzdGaLKcsH2DAsrdsbj00FlFWlY9cdB6Pw1JD",
      ""
    ))(implicitly[ActorSystem])
    println("Post request")
    val fut =
      client
      .userTimelineForUser( screen_name = "test", count = 2).map(_.data.map(_.text.toUpperCase + "!"))

      fut.onComplete{
        case Success(result) => println(result)
        case Failure(t) => t.printStackTrace()
        case _ => println("Dunno")
      }

    Await.result(fut, 5 minutes)
  }


}
