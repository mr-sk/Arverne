Arverne
=======

Algorithmic Trading -- Much to do, not much to talk about at this point. Just laying out the framework. 


Installation

* Java
  * apt-get install openjdk-6-jre  
  * apt-get install openjdk-6-jdk
* R
  * apt-get install r-base r-base-dev
* Gradle (Can't use apt-get. See http://www.mail-archive.com/s4-dev@incubator.apache.org/msg00209.html)
  * wget http://services.gradle.org/distributions/gradle-1.10-bin.zip
  * mv gradle-1.10-bin.zip /opt
  * unzip gradle-1.10-bin.zip
  * ln -s /opt/gradle-1.10 gradle
  * ln -s /opt/gradle/bin/gradle /usr/sbin/gradle
  * gradle -v (does it work?)
* Git
  * apt-get install git 
* Clone repo
  * git clone https://github.com/mr-sk/Arverne
  

Application Setup

* Settings
  * cp sessionSettings.fill-in sessionSettings.txt 
  * Fill out sessionSettings.txt
  

Running
  * gradle build
  * gradle run
