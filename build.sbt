
sbtPlugin := true
organization := "com.lxhub"
name := "lx-sbt-plugin"
scalaVersion := "2.10.5"
version := "0.0.1"
// MOAR

// plugin: sbt-git
git.useGitDescribe := true

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
