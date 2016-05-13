Nice.javaProject

name := "angulillos-neo4j"

description := "angulillos-neo4j project"

organization := "bio4j"

bucketSuffix := "era7.com"

javaVersion := "1.8"

libraryDependencies ++= Seq(
  "org.neo4j" % "neo4j"      % "2.3.3",
  "bio4j"     % "angulillos" % "0.7.0"
)

// dependencyOverrides += "net.sf.opencsv" % "opencsv" % "2.3"
