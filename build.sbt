name := "slick-multidb"

version := "1.0"

scalaVersion := "2.11.8"

mainClass in Compile := Some("SimpleExample")

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.7.21",
  "com.h2database" % "h2" % "1.4.191",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2"
)

fork in run := true
