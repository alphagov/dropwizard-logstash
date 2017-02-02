#!/usr/bin/env bash

set -e

./gradlew --daemon --parallel clean build 2>&1

echo "pre-commit: PASS"