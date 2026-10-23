#!/usr/bin/env bash
# Usage: ./run-tests.sh [full|smoke|regression|negative|demo] [-Dheadless=true]
SUITE="${1:-full}"; shift
case "$SUITE" in
  smoke)      FILE=testng-smoke.xml ;;
  regression) FILE=testng-regression.xml ;;
  negative)   FILE=testng-negative.xml ;;
  demo)       FILE=testng-defect-demo.xml ;;
  *)          FILE=testng.xml ;;
esac
mvn clean test -DsuiteXmlFile="$FILE" "$@"
