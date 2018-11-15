name := """kamon-play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,JavaAppPackaging,JavaAgent)
  //.disablePlugins(PlayLogback)
//javaAgents += "org.aspectj" % "aspectjweaver" % "1.8.13"

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)


val kamonVersion_1_1_0 = "1.1.0"
val kamonVersion_1_0_0 = "1.0.0"

libraryDependencies += "io.kamon" %% "kamon-core" % kamonVersion_1_1_0
libraryDependencies += "io.kamon" %% "kamon-play-2.5" % kamonVersion_1_1_0
libraryDependencies += "io.kamon" %% "kamon-system-metrics" % kamonVersion_1_0_0
libraryDependencies += "io.kamon" %% "kamon-statsd" % kamonVersion_1_0_0
libraryDependencies += "io.kamon" %% "kamon-prometheus" % "1.1.1"
libraryDependencies += "org.aspectj" % "aspectjweaver" % "1.8.13"

//javaOptions in Universal += "-Dorg.aspectj.tracing.factory=default"

routesGenerator := InjectedRoutesGenerator