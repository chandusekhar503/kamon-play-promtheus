name :=
  """kamon-play-promtheus
    |
  """.stripMargin

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)//,JavaAppPackaging,JavaAgent)
//javaAgents += "org.aspectj" % "aspectjweaver" % "1.8.13"
//javaOptions in Universal += "-Dorg.aspectj.tracing.factory=default"

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.36"
)


val kamonVersion_1_1_0 = "1.1.0"
val kamonVersion_1_0_0 = "1.0.0"
val kamonVersion_1_0_2 = "1.0.2"

libraryDependencies += "io.kamon" %% "kamon-core" % kamonVersion_1_1_0
libraryDependencies += "io.kamon" %% "kamon-play-2.5" % kamonVersion_1_1_0
libraryDependencies += "io.kamon" %% "kamon-executors" % kamonVersion_1_0_0
libraryDependencies += "io.kamon" %% "kamon-system-metrics" % kamonVersion_1_0_0
libraryDependencies += "io.kamon" %% "kamon-prometheus" % "1.1.1"
libraryDependencies += "io.kamon" %% "kamon-logback" % kamonVersion_1_0_2
libraryDependencies += "io.kamon" %% "kamon-jaeger" % kamonVersion_1_0_2
libraryDependencies += "io.kamon" %% "kamon-jdbc" % kamonVersion_1_0_0


libraryDependencies += "org.aspectj" % "aspectjweaver" % "1.8.13"

fork in run := false