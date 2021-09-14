#!/usr/bin/env bash

(./gradlew build -x test >> /dev/null) && \

  java \
  -jar ./build/libs/clarity-code-challenge.jar $@
