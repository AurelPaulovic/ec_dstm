organization := "com.aurelpaulovic"

name := "ec_dstm"

version := "0.1"

scalaVersion := "2.10.3"

resolvers += "Sonatype (releases)" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "org.zeromq" % "jzmq" % "3.0.1"

libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT"

scalacOptions ++= Seq( "-feature", "-deprecation", "-language:implicitConversions" )
