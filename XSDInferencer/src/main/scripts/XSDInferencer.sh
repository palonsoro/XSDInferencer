#!/bin/bash
#Max heap size. At least 1024MB is recommended.
MEM_MAX=1024
#0.0.1-SNAPSHOT will be replaced during maven build
java -Xmx${MEM_MAX} -jar XSDInferencer-JAR_VERSION.jar "$@"