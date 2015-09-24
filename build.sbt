
sbtPlugin := true
organization := "com.lxhub"
name := "lx-sbt-plugin"
scalaVersion := "2.10.5"

// plugin: sbt-git
git.useGitDescribe := false
git.baseVersion := "0.0.2-SNAPSHOT"

// huh.

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
  .enablePlugins(BuildInfoPlugin, GitBranchPrompt, GitVersioning)
  .settings(buildinfoPluginSettings: _*)
