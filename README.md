In order to use this, you need a distribution of Java that includes Swing. Java-8-ea comes with JavaFX, NOT Swing. My recommendation is to install the latest JDK from Oracle:

```
sudo apt-get update && sudo apt-get install oracle-java7-jdk
```

If you already have an alternative Java implementation, such as OpenJDK or java-8-early-access, you may need to do the following to switch to the Oracle JDK 7: 

```
sudo update-java-alternatives -s jdk-7-oracle-armhf
```

Of course, you should have 'omxplayer' installed as well...
