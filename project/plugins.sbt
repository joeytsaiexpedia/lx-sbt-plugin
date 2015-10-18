
// Add LxSbtPlugin itself, stolen from https://github.com/sbt/sbt-release/blob/master/project/plugins.sbt
unmanagedSourceDirectories in Compile += baseDirectory.value.getParentFile / "src" / "main" / "scala"

// https://github.com/sbt/sbt-git
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

// https://github.com/sbt/sbt-buildinfo
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.5.0")

// Silence sbt console message SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.12"