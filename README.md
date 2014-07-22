Android Error Tracking for gh-pages
===================================

[![Build Status](https://travis-ci.org/kibotu/net.kibotu.android.error.tracking.svg)](https://travis-ci.org/kibotu/net.kibotu.android.error.tracking)

### Introduction

Library to list errors of your live app on your github.io page.

### How to use

1. Add the lib to your project. Whether by copy & paste of the /jar/error-tracking-lib.jar or by including it into your maven pom.xml.
2. Add Parse.com Android lib to your project. [Latest Parse.com Android SDK Version](https://parse.com/downloads/android/Parse/latest)
3. Init the error tracking lib:
```
ErrorTracking.startSession(this, applicationId, clientKey); // Note: you get the ids by creating a parse.com application.
```
4. Add the [demo html code to your gh-page](https://github.com/kibotu/net.kibotu.android.error.tracking/tree/gh-pages).

5. Init parse in the [main.js:3](https://github.com/kibotu/net.kibotu.android.error.tracking/blob/gh-pages/javascripts/main.js)

## Tests

A working example can be found in the error-tracking-app folder.

### Ready to use lib in /jar/error-tracking-lib.jar

### Ready to use mvn-repo

```
<dependency>
    <groupId>net.kibotu.android.error.tracking</groupId>
    <artifactId>error-tracking-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

```
<repositories>
    <repository>
        <id>net.kibotu.android.error.tracking-mvn-repo</id>
        <url>https://raw.github.com/kibotu/net.kibotu.android.error.tracking/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

### Features

Sends uncaught exceptions of your live app to parse.com and lists them on your gh-page. 

### Changelog

### TODO

* write readme file :D 

### Known Issues

## Contact

**Contributers:**

* [Jan Rabe](mailto:jan.rabe@wooga.com)

## Dependancies 

* Parse.com for throwable storage.

## See also