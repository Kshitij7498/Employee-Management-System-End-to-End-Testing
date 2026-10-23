@echo off
REM Usage: run-tests.bat [full|smoke|regression|negative|demo] [-Dheadless=true]
set SUITE=%1
if "%SUITE%"=="" set SUITE=full
set FILE=testng.xml
if "%SUITE%"=="smoke" set FILE=testng-smoke.xml
if "%SUITE%"=="regression" set FILE=testng-regression.xml
if "%SUITE%"=="negative" set FILE=testng-negative.xml
if "%SUITE%"=="demo" set FILE=testng-defect-demo.xml
mvn clean test -DsuiteXmlFile=%FILE% %2 %3
