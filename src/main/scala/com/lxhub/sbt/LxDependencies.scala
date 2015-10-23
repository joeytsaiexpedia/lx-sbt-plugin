package com.lxhub.sbt

import sbt._

/**
 * Created by a-jotsai on 10/23/15.
 */
object LxDependencies {

  private val scalatest = "org.scalatest" %% "scalatest" % "2.0"
  private val logback = "ch.qos.logback" % "logback-classic" % "1.0.13"
  private val jettyApp = "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106"
  private val jettyServlet = "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016"
  private val janino = "org.codehaus.janino" % "janino" % "2.6.1"
  private val aws = "com.amazonaws" % "aws-java-sdk" % "1.8.9.1"
  private val actuarius = "eu.henkelmann" % "actuarius_2.10.0" % "0.2.6"
  private val mockito = "org.mockito" % "mockito-core" % "1.9.0"

  val cronDeps = Seq(
    scalatest % "it,test",
    mockito % "it,test",
    scalatest % "it",
    logback % "container;compile;runtime",
    jettyApp % "container;compile",
    jettyServlet % "container;provided;it,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    janino,
    aws,
    actuarius % "container;compile;runtime;test"
  )
}
