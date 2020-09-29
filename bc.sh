#!/usr/bin/env bash
mvn install package -Dmaven.test.skip=true
scp -P 22 target/lite-monitor-0.0.1-SNAPSHOT.jar admin@172.16.158.55:/home/admin/lite-monitor/lite-monitor-0.0.1-SNAPSHOT.jar
