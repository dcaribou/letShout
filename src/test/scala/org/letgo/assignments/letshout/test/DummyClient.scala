package org.letgo.assignments.letshout.test

// Dummy 'Twitter' client for testing. Not really talking to twitter, of course :)
object DummyClient {
 def echo(user : String, n : Int) = Seq.fill(n)(user)
}
