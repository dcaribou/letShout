package org.letgo.assignments.letshout.util

import java.nio.file.{Files, Paths}
import twitter4j.{TwitterException, TwitterFactory}
import twitter4j.auth.AccessToken

object AuthenticateApplication {
  def main(args: Array[String]): Unit = {
    val twitter = TwitterFactory.getSingleton()
    twitter.setOAuthConsumer(args(0), args(1))
    val requestToken = twitter.getOAuthRequestToken()
    var accessToken : AccessToken = null
    while(null == accessToken) {
      println("Open the following URL and grant access to your account:")
      println(requestToken.getAuthorizationURL)
      println("Enter the PIN(if aviailable) or just hit enter.[PIN]:")
      val pin = scala.io.StdIn.readLine()
      try{
        if (pin.length > 0) accessToken = twitter.getOAuthAccessToken(requestToken, pin)
        else accessToken = twitter.getOAuthAccessToken
        println("Congratulations! Your application is now authenticated to use the twitter API.")
        Files.write(Paths.get("auth"), s"${accessToken.getToken}\n${accessToken.getTokenSecret}".getBytes())
      }
      catch {
        case te: TwitterException  =>
          if (401 == te.getStatusCode) System.out.println("Unable to get the access token.")
          else te.printStackTrace
          sys.exit()
      }
    }
  }
}
