#!/bin/sh

exec /usr/bin/java -Xms128m -Xmx256m -jar target/replicaza-0.0.1-jar-with-dependencies.jar config.properties

