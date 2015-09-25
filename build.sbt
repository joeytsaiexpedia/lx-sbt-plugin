
//sbtPlugin := true
organization := "com.lxhub"
name := "lx-sbt-plugin"
scalaVersion := "2.10.5"
publishMavenStyle := true

// plugin: sbt-git
// Monotonically increasing version so most recent build is latest
// For brevity, use the first 7 characters of the sha1
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
  .enablePlugins(BuildInfoPlugin, GitBranchPrompt, GitVersioning)
  .settings(buildinfoPluginSettings: _*)
