#!/bin/bash

set -e

export GRAALVM_HOME=/opt/graalvm-ce-java17-21.3.0/
export PATH=$GRAALVM_HOME/bin:$PATH

tag=$1
lein clean
lein test
git add .
git commit -m "release ${tag}"
git tag -a "${tag}" -m "release ${tag}"
docker build -t mcorbin/kvert:${tag} .
docker push mcorbin/kvert:${tag}

lein uberjar
native-image --report-unsupported-elements-at-runtime \
             --initialize-at-build-time \
             --no-server \
             -jar ./target/uberjar/kvert-*-standalone.jar \
             -H:Name=./target/kvert-${tag}
git push --tags
git push
