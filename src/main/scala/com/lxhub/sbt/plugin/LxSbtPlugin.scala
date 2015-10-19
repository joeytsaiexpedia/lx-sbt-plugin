package com.lxhub.sbt.plugin

import com.typesafe.sbt.GitPlugin.autoImport._
import com.typesafe.sbt.{GitBranchPrompt, GitVersioning}
import org.scalastyle.sbt.ScalastylePlugin._
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

/**
 * Common settings between the LX Scala projects.
 *
 * Recognize that changing this project affects all of them, so proceed with caution.
 *
 * Created by a-jotsai on 9/26/15.
 */
object LxSbtPlugin extends AutoPlugin {
  object autoImport {
    lazy val lxClasspathJars = TaskKey[Unit]("lx-classpath-jars", "Get lx jars in classpath")
  }

  import autoImport._

  // noTrigger so users must use this via enablePlugin, but then we can include other plugins
  override def requires: Plugins = plugins.JvmPlugin && BuildInfoPlugin && GitBranchPrompt && GitVersioning

  private val lxSnapshotRepo = "lxhub snapshots" at "https://nexus.lxhub.com/content/repositories/snapshots/"
  private val lxReleaseRepo = "lxhub releases" at "https://nexus.lxhub.com/content/repositories/releases/"

  override val projectConfigurations: Seq[Configuration] = Seq(IntegrationTest)

  override def projectSettings: Seq[Setting[_]] = Defaults.coreDefaultSettings ++ Defaults.itSettings ++
    commonSettings ++ pluginSettings

  private lazy val commonSettings = Seq[Setting[_]](
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
      "-Xmax-classfile-name", "100", // classfile max needed to build on docker
      "-encoding", "UTF-8",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-target:jvm-1.7",
      "-Ywarn-dead-code",
      "-Xlint",
      "-Xfatal-warnings"
    ),

    lxClasspathJars <<= (target, fullClasspath in Runtime) map { (target, cp) =>
      println(s"lx classpath jars: ${cp.map(_.data.toString).filter(_.contains("com.lxhub")).mkString("\n")}")
    },

    // Silence sbt console message SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
    libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.12"
  )

  private lazy val pluginSettings = Seq[Setting[_]](
    // plugin: scalastyle-sbt-plugin
    scalastyleFailOnError := true,

    // plugin: sbt-git
    // Monotonically increasing version so most recent build is latest
    // version will be {gitBaseVersion}-{publishTime}-{first 7 chars of gitSha1}{-SNAPSHOT if there are uncommited changes}
    // consumers should define git.baseVersion
    git.useGitDescribe := false,
    git.baseVersion := "0.0.1",
    git.formattedShaVersion := {
      val ver = git.gitHeadCommit.value map { s => s"${git.formattedDateVersion.value}-${s.take(7)}" }
      if (git.gitUncommittedChanges.value) ver.map { v => s"$v-SNAPSHOT" } else ver
    },

    // plugin: sbt-buildinfo
    buildInfoKeys := Seq[BuildInfoKey](
      name, version, scalaVersion, sbtVersion,
      // get the head sha from sbt-git
      "gitHeadCommit" -> git.gitHeadCommit.value.getOrElse("")
    ),
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoPackage := "com.lxhub.sbt"
  )

}
