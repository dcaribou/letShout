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

Basically we found two good candidates to relay on the job of connecting to the twitter statsues endpoint and get the information for us in the application. The [twitter4s](https://github.com/DanielaSfregola/twitter4s) (Twitter for Scala) from Daniela Sfregola and the [twitter4j](https://github.com/yusuke/twitter4j) (Twitter for Java) from Yusuke Yamamoto. We could not decide which one to use, so we went on and supported both libraries :smile: By setting the property `twitter.lib` in the configuration file to `Twitter4S` or `Twitter4J` the application will use one or the other as the undelying engine communicating to Twitter.

## Akka HTTP

I assume the reader knows about this nice technology that makes it possible to create HTTP REST services in a simple and scalable way. We also made use of the 'caching' module to provide caching functionality for our requests.

# How is it designed?

The application is built around the idea that the akka-supported HTTP server and engine handling the query to the Twitter API should be independet, this way we are able to test the server endpoints and the background twitter engine separately. Therefore, we implemented a `RouteFactory` that is actually creating an Akka Route by connecting an `endpoint` and a `handler` function for the incoming requests on that endpoit. This `handler` can be using undeneath any 3rd party or custom library to talk to twitter as long as it implements the `PluggableShouter` trait. For example, in order to support both `twitter4s` and `twitter4j` we created a wrapper class aroung each library implementing the trait `PluggableShouter`. Then, in our main method, we just need to create an instance of a `PluggableShouter` and connect it to an endpoint `String` to create a `Route`, that we can pass to the binder function.

```scala
val shouter = PluggableShouter() //Creates an instance of the 'default' pluggable shouter
val route : Route  = "letshout" ~> shouter.getShoutedTweets // Creates an Akka Route by connecting a String to a pluggable shouter
// Call the binder function
```

Now the REST server can be queried like

```bash
curl http://localhost/letshout?user=myuser?n=2
```

## Configuration parameters

In order to handle configuration we use an `application.conf` file that we read using `Config` objects from Typesafe. A sample configuration file looks like this (keys are not provided in this sample for safety):

```properties
server.interface = "localhost"
server.port = 12000
server.endpoint = "letshout"
twitter.lib = "Twitter4S"
twitter.consumer.key = <some-key>
twitter.consumer.secret = <some-key>
twitter.access.key = <some-key>
twitter.access.secret = <some-key>
```

With the `twitter.lib` parameter we can select the twitter helper library that will be used to issue the requests to Twitter. The `server.endpoint` defines the name of the endpoint the server will listen for requests, and the keys are static values required for authentication.
A `log4j.properties` file is shipped as well to control the logging.

# How is testing performed?

Thanks to this modular design, we are able to test the different componets separately. There are tests for testing the twitter4j and twitter4s wrappers, a test for the endpoints from the `RouteFactory` and a full test that issues and HTTP request to a running instance of the server.

# How is the server run?

The deployment package is shipped witha a script for runnning the server, with a version for Linux and Windows (.sh and .bat). The server can be running the script. No parameters are required.

```bash
bash start_server.sh
```

## Is it necessary to run the authenticator?

In how case, we ship an `application.conf` version of the config file that already contains all the necessary keys to authenticate the client request, so it should not be necessary to run the authenticator. However, if you need to, you can run it the following script, which is shipped together with the application as well.

```bash
bash run_authenticator.sh $CONSUMER_KEY $CONSUMER_SECRET
```

It takes two parameters, the consumer key and secret, and provides a URL that the user will need to access to get a PIN number that he has to type in to get the access keys:

```console
>java -cp "target/lib/*;target/letShout-1.0.0-SNAPSHOT.jar" -Dconfig.file=src/main/resources/application.conf  org.letgo.assignments.letshout.util.AuthenticateApplication 70xpnqEQvH8SCkJ207dRYfqaB QOsrE7r0ArQBUMMa5J0r7FRM8K5gfbQR61uqyH5Ncbn6b0Am6U
Open the following URL in a web browser to grant access to your account:
https://api.twitter.com/oauth/authenticate?oauth_token=Li-MlQAAAAAA5c_-AAABYsSD_y4
Then enter the provided PIN number or just hit enter if non is provided
XXXXX

Authentication successful!
Access key : XXXX
Access secret : XXXX
```

That can be copied not into the application.conf file to the correspondig parameters.

# What where the most challenging points?
* The authetication and security stuff was quite anoying.
* Marshallers and unmarshallers. I had to play around with them to return and parse JSON objects in the server and in the tests.
* Handling futures in Twitter4S: The twitter4s library concept of returning scala `Future`s does not quite fit our design pattern for a PluggableShouter, so we had so we had to block on the call to `userTimelineForUser` to make it work, which kind of misses the whole point of the library. With a little more time I would look for a better solution there.

# How much time?

Around 16 hours. Splitted among:
* 1 hours for looking for information on the internet
* 2 hours of designing
* 6 hours of coding
* 3 hours of testing
* 3 hours documenting

I was suprised to find out it took me this long, but I think it's quite accurate.