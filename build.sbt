name := "spectator"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8",
  jdbc,
  anorm,
  cache
)

play.Project.playScalaSettings
