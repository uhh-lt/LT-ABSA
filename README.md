[![Build Status](https://travis-ci.org/uhh-lt/LT-ABSA.svg)](https://travis-ci.org/uhh-lt/LT-ABSA)
[![Release](https://jitpack.io/v/uhh-lt/LT-ABSA.svg)](https://jitpack.io/#uhh-lt/LT-ABSA)
# Aspect-Based Sentiment Analysis

##  Overview
This software package performs aspect-based sentiment analysis. It can analyze documents and identify aspect targets, their aspect category and their relevance. For usage in live systems, it features a relevance classifier to filter irrelevant documents.
JavaDoc documentation is available on the [documentation page](http://tudarmstadt-lt.github.io/AB-Sentiment/doc/).

## Quickstart

* create a new project
* add the jitpack Maven dependency
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
* add the AB-Sentiment dependency
```
	<dependency>
	    <groupId>com.github.uhh-lt</groupId>
	    <artifactId>LT-ABSA</artifactId>
	    <version>-SNAPSHOT</version>
	</dependency>
```
* copy models into your project home
* create a Java class for analysis:
```
import uhh_lt.ABSA.ABSentiment.type.Result;
import uhh_lt.ABSA.ABSentiment.AbSentiment;

public class MyClass {

    public static void main(String[] args) {
        AbSentiment analyzer = new AbSentiment();

        Result result = analyzer.analyzeText("This is the input string");

        // get Sentiment of text
        System.out.println(result.getSentiment());
        System.out.println(result.getSentimentScore());

    }
}
```
* analyze aspects and sentiment :)


## Licence
This software is published under the Apache Software Licence 2.0

## Third party license
This software uses the jblas library. The copyright link is : https://github.com/mikiobraun/jblas/blob/e1de8249b28137fa94a79558ee90ff037fd7c47d/COPYING
