name := "Sample Project"
 
version := "1.0"
 
scalaVersion := "2.11.8"
 
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-target:jvm-1.8",
  "-unchecked",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Xlint"
)
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.1" % "compile",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0" % "compile"
)

