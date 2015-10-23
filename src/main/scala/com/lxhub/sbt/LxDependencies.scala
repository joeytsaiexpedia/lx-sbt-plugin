package com.lxhub.sbt

import sbt._

/**
 * All dependencies which are used by our Scala projects are defined here.  Although it may seem more natural to define
 * library dependencies at the project level, the reality of our related projects is we are supporting a collection of
 * dependencies, many with dependencies of their own.  Therefore, we need to ensure that all our stack of libraries is
 * known to work together, so versions should be defined in a single place.
 *
 * Please be very careful about adding dependencies.  There are several reasons to be cautious:
 * 1) We have to support every dependency we add - any time we interact with another library we must learn and work
 *    around its implementation.  Simply adding a dependency increases this surface area we must support.
 * 2) Dependencies often have their own dependencies that can (and have) conflicted with other dependencies, which leads
 *    to increased maintenance and hard-to-debug issues.
 * 3) Our tools (SBT and IntelliJ) must manage all of our library dependencies, so increasing the number of dependencies
 *    will slow the build process for all developers.
 *
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
