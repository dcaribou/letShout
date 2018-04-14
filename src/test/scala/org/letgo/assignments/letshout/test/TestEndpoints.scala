package org.letgo.assignments.letshout.test

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.letgo.assignments.letshout.{Twitter4JWrapper, LetShout, RouteFactory}
import org.scalatest.{ Matchers, WordSpec }

import scala.util.{Failure, Success}

// Test the endpoint is created as expected and parameters are recognized
class TestEndpoints extends WordSpec with Matchers with ScalatestRouteTest {
  import org.letgo.assignments.letshout.Implicits._
  import org.letgo.assignments.letshout.JsonSupport._
  val testRoute = "test" ~> DummyClient.echo
  "LetShout" should {
    "reply in time when a request is received in the listening endpoint" in {
      Get("/test?user=testuser&n=3") ~> testRoute ~> check {
        responseAs[Seq[String]] shouldEqual Seq("testuser", "testuser", "testuser")
      }
    }
  }
}
