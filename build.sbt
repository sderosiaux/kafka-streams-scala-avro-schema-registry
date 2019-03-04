name := "kstreams-scala"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.1.1"
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.1.0"

resolvers ++= Seq(
    "confluent" at "https://packages.confluent.io/maven",
    Resolver.mavenLocal
)

libraryDependencies ++= Seq(
    "io.confluent" % "kafka-avro-serializer" % "5.1.2",
    "io.confluent" % "kafka-streams-avro-serde" % "5.1.2"
)

sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue