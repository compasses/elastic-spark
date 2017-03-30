resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal)

name := "Flink Project"

version := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

val flinkVersion = "1.2.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % "compile",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "compile",
  "org.apache.flink" % "flink-connector-elasticsearch2_2.10" % "1.2.0" % "compile",
  "org.apache.flink" % "flink-connector-rabbitmq_2.10" % "1.2.0" % "compile"
)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
  )

//mainClass in assembly := Some("org.example.Job")
//
//// make run command include the provided dependencies
//run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
//
//// exclude Scala library from assembly
//assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
