[![Build Status](https://travis-ci.org/uhh-lt/LT-ABSA.svg)](https://travis-ci.org/uhh-lt/LT-ABSA)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/be3edf328f014b6c9e0c2f327d650ff0)](https://www.codacy.com/app/eugenso/LT-ABSA?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=uhh-lt/LT-ABSA&amp;utm_campaign=Badge_Grade)
[![Release](https://jitpack.io/v/uhh-lt/LT-ABSA.svg)](https://jitpack.io/#uhh-lt/LT-ABSA)

# Aspect-Based Sentiment Analysis

##  Overview
This software package performs aspect-based sentiment analysis. It can analyze documents and identify aspect targets, their aspect category and their relevance. For usage in live systems, it features a relevance classifier to filter irrelevant documents.
JavaDoc documentation is available on the [documentation page](https://tudarmstadt-lt.github.io/AB-Sentiment/doc/).

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
* adapt the configuration file with the correct paths
* create a Java class for analysis:
```
import uhh_lt.ABSA.ABSentiment.type.Result;
import uhh_lt.ABSA.ABSentiment.AbSentiment;

public class MyClass {

    public static void main(String[] args) {
        AbSentiment analyzer = new AbSentiment("congiguration.txt");

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


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fuhh-lt%2FLT-ABSA.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fuhh-lt%2FLT-ABSA?ref=badge_large)

## Third party license
This software uses the jblas library. The copyright link is : https://github.com/mikiobraun/jblas/blob/e1de8249b28137fa94a79558ee90ff037fd7c47d/COPYING
