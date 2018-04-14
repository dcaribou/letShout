package org.letgo.assignments.letshout

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
  * Pluggable shouters factory
  */
object PluggableShouter {
  val logger = LoggerFactory.getLogger(getClass)
  def apply(): PluggableShouter =
    ConfigFactory.load().getString("twitter.lib")
    match {
      case "Twitter4S" =>
        logger.debug("Will relay on the fantastic 'twitter4s' by Daniela Sfregola to talk to twitter API")
        Twitter4SWrapper
      case "Twitter4J" =>
        logger.debug("Will relay on the wonderful 'twitter4j' by Yusuke Yamamoto to talk to the twitter API")
        Twitter4JWrapper()
    }
}

/**
  * This trait should be implemented for any client wrapper that aims to be pluggable
  */
trait PluggableShouter {
  def getShoutedTweets(screenName : String, count : Int) : Seq[String]
}
