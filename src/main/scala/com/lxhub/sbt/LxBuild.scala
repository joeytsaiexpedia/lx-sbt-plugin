package com.lxhub.sbt

import com.typesafe.sbt.GitPlugin.autoImport._
import com.typesafe.sbt.{GitVersioning, GitBranchPrompt}
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

/**
 * Created by a-jotsai on 10/17/15.
 */
object LxBuild {
  /*
  def buildInfoKeys
  Seq(
    buildInfoKeys := Seq[BuildInfoKey](
    name, version, scalaVersion, sbtVersion,
    // get the head sha from sbt-git
    BuildInfoKey.map(git.gitHeadCommit) { case (k, v) => k -> v.getOrElse("") }
  ),
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoPackage := "com.lxhub.sbt",
    buildInfoObject := "BuildInfoLxSbtPlugin"
  )
  */
  /*
  def lxProject(): Project = (project in file("."))
    .configs(IntegrationTest)
    .enablePlugins(BuildInfoPlugin, GitBranchPrompt, GitVersioning)
    .settings(Defaults.coreDefaultSettings)
    //.settings(LxhubAdminApiBuild.projectSettings: _*)
    .settings(Defaults.itSettings: _*)
    //.settings(libraryDependencies ++= Dependencies.allDependencies)
    //.settings(buildinfoPluginSettings: _*)
    */
}
