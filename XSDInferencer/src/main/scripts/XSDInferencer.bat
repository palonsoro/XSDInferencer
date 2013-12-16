@echo off
rem Max heap size. At least 1024MB is recommended.
set MEM_MAX=1024
rem JAR_VERSION will be replaced during maven build
java -Xmx%MEM_MAX%m -jar XSDInferencer-JAR_VERSION.jar %*