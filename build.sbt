
sbtPlugin := true
name := "lx-sbt-plugin"
git.baseVersion := "0.4.3"
buildInfoObject := "BuildInfoLxSbtPlugin"

lazy val plugin = (project in file("."))
  .enablePlugins(LxSbtPlugin)

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
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")
