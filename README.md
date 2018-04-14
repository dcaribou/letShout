# What is it?

A simple API REST Server with a single endpoint, which delivers the latest tweets from a Twitter user in capital letters and with a trailing exclamation mark.

# Which technologies is it based on?

## Twitter developers API

Twitter provides an API REST in order for developers to be able to query the data in the website (tweets, mentions, friend lists, trends...). Any properly authenticated application can obtain this information from the API and use it for his own purposes. Further information about twitter API and authentication mechanism can be found in the [official docs for Twitter Developers](https://developer.twitter.com/en/docs)

As it can be seen in the docs, authentication fundamentals are somewhat complex and beyond the scope of this proyect. We will just say, for the sake of a better understanding of the project, that two pairs of keys are required for a correct authentication: the consumer keys and the access keys. Both pairs are provided in the configuration file for simplicity, but the access keys can also be obtained from twitter via a custom authenticator that is shipped with the project which I will explain later.

Back to the twitter API, there is an interesting endpoint that suits perfectly our application: the ['statsues' endpoint](https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html). This would allow us to query the timeline of a given user for the latest N tweets like this:

* GET https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=twitterapi&count=2

Where screen_name is the user name and count is the number of tweets we want to get. This is perfect for our purposes, so we looked for a library that made it simple for use to access this feature.

## Twitter client libraries

Basically we found two good candidates to relay on the job of connecting to the twitter statsues endpoint and get the information for us in the application. The [twitter4s](https://github.com/DanielaSfregola/twitter4s) (Twitter for Scala) from Daniela Sfregola and the [twitter4j](https://github.com/yusuke/twitter4j) (Twitter for Java) from Yusuke Yamamoto. We could not decide which one to use, so we went on and supported both libraries :). But setting the property 'twitter.lib' in the configuration file to 'Twitter4S' or 'Twitter4J' the application will use one or the other in the background.

## Akka HTTP

I assume the reader knows about this nice technology that makes it possible to create HTTP REST services in a simple and scalable way. We also made use of the 'caching' module to provide caching functionality for our requests.