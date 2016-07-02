#!/bin/bash
if [[ ! -d ./apache-tomcat ]];then
    echo "apache-tomcat is not found"
    exit 1
fi

mvn clean package

cp ./target/world-1.0.war ./apache-tomcat/webapps
