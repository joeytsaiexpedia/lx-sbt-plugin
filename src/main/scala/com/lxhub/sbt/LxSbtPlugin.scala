package com.lxhub.sbt

import com.typesafe.sbt.{GitVersioning, GitBranchPrompt}
import com.typesafe.sbt.GitPlugin.autoImport._
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
    lazy val lxJars = TaskKey[Unit]("lx-jars", "Get lx jars in classpath")
  }

  import autoImport._

  // noTrigger so users must use this via enablePlugin, but then we can include other plugins
  override def requires: Plugins = plugins.JvmPlugin && BuildInfoPlugin && GitBranchPrompt && GitVersioning

  override def projectSettings: Seq[Setting[_]] = Defaults.coreDefaultSettings ++ Defaults.itSettings ++
    commonSettings ++ lxKeys ++ pluginSettings

  override val projectConfigurations: Seq[Configuration] = Seq(IntegrationTest)

  private val lxSnapshotRepo = "lxhub snapshots" at "https://nexus.lxhub.com/content/repositories/snapshots/"
  private val lxReleaseRepo = "lxhub releases" at "https://nexus.lxhub.com/content/repositories/releases/"

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
      // can't enable this since warnings are fatal "-Ywarn-dead-code",
      "-Xlint",
      "-Xfatal-warnings"
    )
  )

  private lazy val lxKeys = Seq[Setting[_]](
    lxJars <<= (target, fullClasspath in Runtime) map { (target, cp) =>
      println(s"lx classpath jars: ${cp.map(_.data.toString).filter(_.contains("com.lxhub")).mkString("\n")}")
    }
  )

  // consumers should define git.baseVersion and buildInfoObject
  private lazy val pluginSettings = Seq[Setting[_]](
    // plugin: scalastyle-sbt-plugin
    scalastyleFailOnError := true,

    // plugin: sbt-git
    // Monotonically increasing version so most recent build is latest
    // version will be {gitBaseVersion}-{publishTime}-{first 7 chars of gitSha1}{-SNAPSHOT if there are uncommitted changes}
    git.useGitDescribe := false,
    git.baseVersion := "0.0.1",
    git.formattedShaVersion := {
      val ver = git.gitHeadCommit.value map { s =>
        // Weird behavior in Sonatype Nexus (which powers nexus.lxhub):
        // If we publish a release version where the first 7 chars of gitSha1 are all numeric,
        // Sonatype Nexus fails with status code 400: Bad Request
        // As a workaround, detect this situation and add some characters.
        val first = s.take(7)
        val sha = if (first.forall(_.isDigit)) s"sha$first" else first
        s"${git.formattedDateVersion.value}-$sha"
      }
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
