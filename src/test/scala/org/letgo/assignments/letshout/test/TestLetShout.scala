package org.letgo.assignments.letshout.test

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import akka.actor.ActorSystem
import org.letgo.assignments.letshout.GetShout
import org.scalatest._

import scala.util.{Failure, Success}


class TestLetShout extends FlatSpec with Matchers {
  val twitterClient =
    GetShout(
      "70xpnqEQvH8SCkJ207dRYfqaB",
      "QOsrE7r0ArQBUMMa5J0r7FRM8K5gfbQR61uqyH5Ncbn6b0Am6U"
    )

  "GetShout" should ""
}
