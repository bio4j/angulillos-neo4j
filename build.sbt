Nice.javaProject

javaVersion := "1.8"

organization  := "bio4j"
name          := "angulillos-neo4j"
description   := "Use Neo4j with the angulillos API"

libraryDependencies ++= Seq(
  "org.neo4j" % "neo4j"      % "2.3.3",
  "bio4j"     % "angulillos" % "0.7.2"
)

bucketSuffix := "era7.com"
