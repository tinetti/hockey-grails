#!/bin/bash

sudo /etc/init.d/tomcat stop
sudo rm -rf /usr/local/tomcat/work/Catalina/localhost/hockey*
sudo rm -rf /usr/local/tomcat/webapps/hockey
grails prod war
sudo /etc/init.d/tomcat start
