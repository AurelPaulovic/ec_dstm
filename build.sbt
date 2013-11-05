organization := "com.aurelpaulovic"

name := "ec_dstm"

version := "0.1"

scalaVersion := "2.10.2"

resolvers += "Sonatype (releases)" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.zeromq" % "jzmq" % "3.0.1"

scalacOptions ++= Seq( "-feature", "-deprecation" )
