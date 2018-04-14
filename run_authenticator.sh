# Run after compiling
java -cp "target/lib/*:target/letShout-1.0.0-SNAPSHOT.jar" -Dconfig.file=src/main/resources/application.conf  org.letgo.assignments.letshout.util.AuthenticateApplication $@