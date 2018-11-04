name := "JSONLinesViewer"

version := "0.1"

scalaVersion := "2.12.7"

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-swing
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.0.3"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.7"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7"

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"