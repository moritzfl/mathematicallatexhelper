#!/bin/bash
mvn install:install-file -Dfile=jai_core-1.1.3.jar -DgroupId=Javax.media -DartifactId=jai_core -Dversion=1.1.3 -Dpackaging=jar
mvn install:install-file -Dfile=mathocr-0.0.3.jar -DgroupId=com.github.chungkwong -DartifactId=mathocr -Dversion=0.0.3 -Dpackaging=jar 