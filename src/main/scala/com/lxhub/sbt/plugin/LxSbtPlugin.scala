package com.lxhub.sbt.plugin

import sbt._
import Keys._
import com.typesafe.sbt.{GitVersioning, GitBranchPrompt}
import sbtbuildinfo.BuildInfoPlugin

/**
 * Common settings between the LX Scala projects.
 *
 * Recognize that changing this project affects all of them, so proceed with caution.
 *
 * Created by a-jotsai on 9/26/15.
 */
object LxSbtPlugin extends AutoPlugin {
  object autoImport {
    lazy val lxPlugin = settingKey[String]("lx is the best")
  }

  import autoImport._

  lxPlugin := "what is happening"

  // noTrigger so we need enablePlugin, but then we can include other plugins
  override def requires: Plugins = plugins.JvmPlugin && BuildInfoPlugin && GitBranchPrompt && GitVersioning

  private val lxSnapshotRepo = "lxhub snapshots" at "https://nexus.lxhub.com/content/repositories/snapshots/"
  private val lxReleaseRepo = "lxhub releases" at "https://nexus.lxhub.com/content/repositories/releases/"

  override val projectConfigurations: Seq[Configuration] = Seq(IntegrationTest)

  override def projectSettings: Seq[Setting[_]] = Defaults.coreDefaultSettings ++ Defaults.itSettings ++
    Seq(
      organization := "com.lxhub",
      scalaVersion := "2.10.5",

      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in (Compile, packageSrc) := false,

      publishMavenStyle := true,
      credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.lxhub.com", "lxpublisher", "password1"),
      publishTo := {
        Some(if (version.value.trim.endsWith("SNAPSHOT")) lxSnapshotRepo else lxReleaseRepo)
      },

      resolvers ++= Seq(
        Resolver.typesafeRepo("releases"),
        Resolver.sonatypeRepo("releases"),
        Resolver.typesafeIvyRepo("releases"),
        Resolver.sbtPluginRepo("releases"),
        lxReleaseRepo
      ),

      scalacOptions := Seq(
        "-encoding", "UTF-8",
        "-deprecation",
        "-unchecked",
        "-feature",
        "-Ywarn-dead-code",
        "-Xlint"
      ),

      // Silence sbt console message SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
      libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.12"

    )

}
