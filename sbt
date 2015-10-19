#!/bin/bash
set +e

# from https://github.com/twitter/finagle/blob/develop/sbt

# ensure this sbtver and project/build.properties are consistent
sbtver=0.13.8
sbtjar=sbt-launch.jar
sbtsha128=57d0f04f4b48b11ef7e764f4cea58dee4e806ffd

sbtrepo=https://nexus.lxhub.com/content/groups/mvn-repos/org.scala-sbt/sbt-launch

if [ ! -f $sbtjar ]; then
  echo "downloading $sbtjar" 1>&2
  if ! curl --location --silent --fail --remote-name $sbtrepo/$sbtver/$sbtjar; then
    exit 1
  fi
fi

checksum=`openssl dgst -sha1 $sbtjar | awk '{ print $2 }'`
if [ "$checksum" != $sbtsha128 ]; then
  echo "bad $sbtjar.  delete $sbtjar and run $0 again."
  exit 1
fi

[ -f ~/.sbtconfig ] && . ~/.sbtconfig

# The following SBT_OPTS will force sbt to use the lxhub nexus.
# Therefore, you must be able to connect to lxhub in order to build
SBT_OPTS="-Dsbt.override.build.repos=true -Dsbt.repository.config=sbtrepo.conf $SBT_OPTS"

java -ea                          \
  $SBT_OPTS                       \
  $JAVA_OPTS                      \
  -Djava.net.preferIPv4Stack=true \
  -Djsse.enableSNIExtension=false \
  -XX:+AggressiveOpts             \
  -XX:+UseParNewGC                \
  -XX:+UseConcMarkSweepGC         \
  -XX:+CMSParallelRemarkEnabled   \
  -XX:+CMSClassUnloadingEnabled   \
  -XX:MaxPermSize=1024m           \
  -XX:SurvivorRatio=128           \
  -XX:MaxTenuringThreshold=0      \
  -XX:ReservedCodeCacheSize=128m  \
  -Xss8M                          \
  -Xms512M                        \
  -Xmx1G                          \
  -server                         \
  -jar $sbtjar "$@"
