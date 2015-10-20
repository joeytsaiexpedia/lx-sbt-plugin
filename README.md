# lx-sbt-plugin
This is a central implementation for the SBT features in use in LX Scala projects.  SBT provides many critical functions in our develop + build + deploy pipeline, so it's essential that SBT is consistent and dependable across all our projects.

SBT is the meta codebase we use to:
* compile our Scala code
* manage our library dependencies
* run our unit and integration tests
* run static analysis tools (scalastyle, scoverage)
* create our deployables
* other build time tasks (versioning, generating files)

Warnings:
* Despite its name, SBT is not simple
* SBT is very important for our engineering team
* Therefore, be very careful about modifying SBT - especially adding new dependencies
  * New dependencies often have their own dependencies, so all the artifacts wil be pulled into multiple projects.  Having a large amount of dependencies will slow down SBT for all engineers!
  * Any of these libraries can (and have) conflicted with other our current dependencies, and this leads to hard to debug issues.
  * Therefore, before changing dependencies ensure all the libraries work together

## This project

This plugin provides
* common settings for Scala and SBT
* enables and configures several other SBT plugins to provide functionality:
  * [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo): Compile-time information about the build - date and time, git sha1, scala version, etc.
  * [sbt-git](https://github.com/sbt/sbt-git): Git integration - do git commands inside sbt, git also does our versions
  * [scalastyle](http://www.scalastyle.org/sbt.html): Scalastyle for static analysis.
* some custom commands:
  * lxJars (lx-jars): Shows the lx jars included at runtime

Future features:
* centralized library dependencies
* more static analysis, scoverage
* Scala 2.11 (finally!)
* better jenkins integration
* same tomcat deployment as production
* docker integration