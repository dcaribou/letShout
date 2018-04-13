package org.letgo.assignments.letshout.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.letgo.assignments.letshout.{Twitter4JWrapper, Tweets}
import org.scalatest.{FlatSpec, Matchers, WordSpec}

import scala.util.{Failure, Success}
import scala.concurrent._
import scala.concurrent.duration._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import org.scalatest.AsyncFlatSpec

class TestLetShout extends AsyncFlatSpec with Matchers  {
  import org.letgo.assignments.letshout.Implicits._
  val config = ConfigFactory.load()
  implicit val actorSystem = ActorSystem(config.getString("server.akka.as-name"))
  implicit val materializer = ActorMaterializer()
  val twitter4jBasedHandler = (user : String, n : Int) =>
    Twitter4JWrapper().getShoutedTweets(user, n)
  // Bind server to an interface and start listening
  val bindingFuture =
    Http().bindAndHandle(
      "letshout" ~> twitter4jBasedHandler,
      "localhost",
      config.getInt("server.port")
    )
  "LetShout" should "get latest from a known twitter account in capital letters" in {
      Http().singleRequest(
        HttpRequest(
          uri = s"http://localhost:${config.getInt("server.port")}/${config.getString("server.endpoint")}?user=dcaramu&n=3"
        )
      ) map {
        case HttpResponse(status,_,entity,_) =>
          val contents = Await.result(
            entity.dataBytes.runFold(ByteString(""))(_ ++ _),
            10 seconds
          ).utf8String
          assert(contents == "[\"THIRD TWEET!\",\"SECOND TWEET!\",\"FIRST TWEET!\"]" && status == StatusCodes.OK)
      }
  }
}
