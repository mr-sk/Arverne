Arverne
=======

Algorithmic Trading -- Much to do, not much to talk about at this point. Just laying out the framework. 


Installation

* R
  * apt-get install r-base r-base-dev
* Gradle (Can't use apt-get. See http://www.mail-archive.com/s4-dev@incubator.apache.org/msg00209.html)
  * wget http://services.gradle.org/distributions/gradle-1.10-bin.zip
  * mv gradle-1.10-bin.zip /opt
  * unzip gradle-1.10.bin.zip
  * sudo ln -s /opt/gradle-1.10 gradle
  * sudo ln -s /opt/gradle/bin/gradle /usr/sbin/gradle
  * gradle -v (does it work?)
  

Running
  * gradle build
  * gradle run
