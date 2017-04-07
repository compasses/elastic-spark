name := "scala computing"
 
version := "1.0"
 
scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused"
)
lazy val akkaVersion = "2.4.4"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.1" % "compile",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0" % "compile",
  "org.apache.spark" % "spark-mllib_2.11" % "2.1.0" % "compile",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion % "compile",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "compile"

)

