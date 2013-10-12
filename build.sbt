name := "spectator"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "0.5.0.8",
  jdbc,
  anorm,
  cache
)

play.Project.playScalaSettings
