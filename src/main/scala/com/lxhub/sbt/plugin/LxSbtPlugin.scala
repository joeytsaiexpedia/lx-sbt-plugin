package com.lxhub.sbt.plugin

import com.typesafe.sbt.GitPlugin.autoImport._
import com.typesafe.sbt.GitPlugin.autoImport._
import com.typesafe.sbt.GitPlugin.autoImport._
import org.scalastyle.sbt.ScalastylePlugin._
import sbt._
import Keys._
import com.typesafe.sbt.{GitVersioning, GitBranchPrompt}
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
    lazy val lxBaseVersion = settingKey[String]("lx uses GitVersioning; alias for git.baseVersion")
  }

  import autoImport._

  lxBaseVersion := "0.0.1"

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
      "gitHeadCommit" -> git.gitHeadCommit.value.orNull
    ),
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoPackage := "com.lxhub.sbt"
  )

}
