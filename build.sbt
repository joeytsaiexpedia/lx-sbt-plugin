
sbtPlugin := true
name := "lx-sbt-plugin"

// plugin: sbt-git
// Monotonically increasing version so most recent build is latest
// version will be {gitBaseVersion}-{publishTime}-{first 7 chars of gitSha1}
git.useGitDescribe := false
git.baseVersion := "0.4.2"
git.formattedShaVersion := git.gitHeadCommit.value map { s => s"${git.formattedDateVersion.value}-${s.take(7)}" }

// plugin: sbt-buildinfo
lazy val buildinfoPluginSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name, version, scalaVersion, sbtVersion,
    // get the head sha from sbt-git
    BuildInfoKey.map(git.gitHeadCommit) { case (k, v) => k -> v.getOrElse("") }
  ),
  buildInfoOptions += BuildInfoOption.BuildTime,
  buildInfoPackage := "com.lxhub.sbt",
  buildInfoObject := "BuildInfoLxSbtPlugin"
)

lazy val plugin = (project in file("."))
  .enablePlugins(LxSbtPlugin)
  .settings(buildinfoPluginSettings: _*)

val clearLocal = Def.task {
  val log = streams.value.log
  log.info("Removing old versions of lx-sbt-plugin ...")
  IO.delete(Path.userHome / ".ivy2" / "local" / "scala_2.10" / "sbt_0.13" / "com.lxhub" / "lx-sbt-plugin_2.10")
  IO.delete(Path.userHome / ".ivy2" / "local" / "com.lxhub" / "lx-sbt-plugin_2.10")
  IO.delete(Path.userHome / ".ivy2" / "local" / "com.lxhub" / "lx-sbt-plugin" / "scala_2.10" / "sbt_0.13")
  log.info("... removal complete.")
}

publishLocal := {
  val cl = clearLocal.value
  publishLocal.value
}

// Ensure the below is consistent with project/plugins.sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.5.0")
