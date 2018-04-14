package org.letgo.assignments.letshout.util

import java.nio.file.{Files, Paths}

import twitter4j.{Twitter, TwitterException, TwitterFactory}
import twitter4j.auth.{AccessToken,RequestToken}

object AuthenticateApplication {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      print(
        """
          |The authenticator requires two parameters: consumer key and consumer secret
        """.stripMargin)
      sys.exit()
    }
    // Obtain an instance of the twit4j client
    implicit val twitter = TwitterFactory.getSingleton()
    // Set consumer key and secret
    twitter.setOAuthConsumer(args(0), args(1))
    // Attempt user-assisted authentication
    processUserAttempt(3) match {
      // If authentication succeeds, stash keys away and finish
      case Some(accessToken) =>
        Files.write(Paths.get("auth"), s"${accessToken.getToken}\n${accessToken.getTokenSecret}".getBytes())
      // If not ust say goodbye
      case None =>
        println("Unable to obtain access tokens, the LetShout server will most likely not work")
    }
  }
  // Request an access token to twitter with the PIN method
  def requestAccessToken(requestToken : RequestToken, pin : String)(implicit twitter : Twitter): Option[AccessToken] = {
    try {
      Some(twitter.getOAuthAccessToken(requestToken, pin))
    }
    catch {
      case e : TwitterException =>
        println(s"Could not obtain access token -> ${e.getMessage}")
        None
    }
  }
  def processUserAttempt(remainingAttempts : Int)(implicit twitter : Twitter): Option[AccessToken] = {
    try {
      // The request token allows for sending authentication requests to the server
      val requestToken = twitter.getOAuthRequestToken()
      // Show the url to the user
      println(
        "Open the following URL in a web browser to grant access to your account:\n" +
        s"${requestToken.getAuthenticationURL}\n" +
        "Then enter the provided PIN number or just hit enter if non is provided"
      )
      // Authenticate application together with the PIN provided by the user
      requestAccessToken(requestToken, scala.io.StdIn.readLine()) match {
        // If the authentication is successful, report it
        case Some(accessToken) => {
          print(
            s"""
              |Authentication successful!
              |Access key : ${accessToken.getToken}
              |Access secret : ${accessToken.getTokenSecret}
            """.stripMargin)
          Some(accessToken)
          //
        }
        // If the authentication is not succesful, report it and reattempt
        case None if remainingAttempts > 0 => processUserAttempt(remainingAttempts - 1)
        case None => {
          println("Authentication failed. Please, review the provided keys and try again.")
          None
        }
      }
    }
    catch {
      case e: TwitterException if e.getErrorCode == 89 =>
        println("Overdue access token. We should not be here, try restarting this authenticator")
        return None
      case e: TwitterException if e.getMessage == "Connection reset" =>
        println(s"Connection to twitter failed. Are you maybe behind a proxy?")
        return None
      case e: TwitterException => {
        println(s"Twitter error: ${e.getMessage}")
        return None
      }
    }
  }
}
