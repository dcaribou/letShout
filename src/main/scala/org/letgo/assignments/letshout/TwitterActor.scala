package org.letgo.assignments.letshout

import akka.actor.{Actor, ActorSystem}
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.http.clients.rest.statuses.TwitterStatusClient
import org.letgo.assignments.letshout.entities.ShoutRequest

class TwitterActor(consumerToken: ConsumerToken, accessToken: AccessToken) extends Actor {
  //val restClient = new RestClient(consumerToken, AccessToken(key = "my-access-key", secret = "my-access-secret")  )
  val client = TwitterRestClient.withActorSystem(consumerToken, accessToken)(context.system)
  override def preStart(): Unit = {
    //TODO: Check the provided access tokes actually grant access to the twitter api
  }
  override def receive: Receive = {
    case ShoutRequest(user, n) => client.userTimelineForUser( screen_name = user, count = n)
  }
}
