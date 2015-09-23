package com.lxhub.sbt.plugin

import com.lxhub.sbt.BuildInfoLxSbtPlugin

/**
 * Created by a-jotsai on 9/23/15.
 */
object Herro extends App {
  val ver = BuildInfoLxSbtPlugin.version
  val sha = BuildInfoLxSbtPlugin.gitHeadCommit
  println(s"oh hai, my version=$ver sha1=$sha")
  // make more changes.
  // just to be sure the version doesn't get updated by sbt-git
}
