# Hadolint SonarQube Plugin
[![Build Status](https://travis-ci.org/cnescatlab/sonar-hadolint-plugin.svg?branch=dev)](https://travis-ci.org/cnescatlab/sonar-hadolint-plugin)
[![SonarQube Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin)
[![SonarQube Bugs](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin&metric=bugs)](https://sonarcloud.io/dashboard?id=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin)
[![SonarQube Coverage](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin)
[![SonarQube Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin&metric=sqale_index)](https://sonarcloud.io/component_measures?id=fr.cnes.sonarqube.plugins%3Asonar-hadolint-plugin&metric=Maintainability)

SonarQube plugin for the Dockerfile analysis tool Hadolint.

SonarQube is an open platform to manage code quality. This plugin adds the ability to import pre-existing results of Hadolint in **checkstyle format only**.

This plugin is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

You can get [Hadolint](https://github.com/hadolint/hadolint) on GitHub.

As Hadolint uses [ShellCheck](https://github.com/koalaman/shellcheck) to lint bash code in Dockerfiles, this plugin is inspired by the [Shellcheck SonarQube Plugin](https://github.com/sbaudoin/sonar-shellcheck).

### Known limitations

#### A Dockerfile has no file extension

SonarQube plugins can automatically identified whether they should run, thanks to the file extension.  
For example, a java plugin can easily identify the files it should analyze, with the .java extension.  
We can't use this behaviour for Dockerfiles to know if the plugin should run, because they have no standardized extension.  

There is no way SonarQube can automatically recognize a Dockerfile without declaring an extension inside the plugin.  
However, the solution we found is to explicitly give the Sonar Scanner some patterns to find Dockerfiles, when starting an analysis.  
This way, the plugin will only run if the patterns match at least one file, and Dockerfiles can be fully analyzed.

More details regarding properties are available in the dedicated section below.

### Language, Metrics and Highlighting

The plugin creates a `Dockerfile` language inside SonarQube, linked to Hadolint rules.    
For identified Dockerfiles in your projects, the plugin calculates two metrics : number of comments, and number of lines of code.  
It also provides a basic text highlighting when you check the code on SonarQube web interface.  

### Quickstart
- Setup a SonarQube instance.
- Install Hadolint command line tool.
- Download plugin JAR file from GitHub Releases page, or build it (see dedicated section below)
- Install `sonar-hadolint-plugin-*.jar` in `<SONARQUBE_HOME>/extensions/plugins/`.
- Run Hadolint to produce a report in **checkstyle format**
  - For exemple : `hadolint -f checkstyle Dockerfile > hadolint-report.xml`
- Set the appropriate plugin properties in your *sonar-project.properties*.
- Run an analysis with *sonar-scanner*, *maven*, *gradle*, *msbuild*, etc.

#### Run Hadolint
For now, the plugin is not able to run Hadolint automatically, you have to handle this by yourself.  
Go to the project page to find indications : https://github.com/hadolint/hadolint  
**Be sure to generate reports in checkstyle format !** It is the only one the plugin supports.

#### Plugin's properties
- `sonar.lang.patterns.dockerfile`: 
  - Comma separated list of patterns matching Dockerfiles inside your project.  
  - **/!\\** _Those patterns must match Dockerfiles only ! You will have issues if they match another analyzed language._
  - Default: `Dockerfile`.
- `sonar.hadolint.reports.path`: 
  - Comma separated list of path to Hadolint reports in checkstyle format.
  - Default: `hadolint-report.xml`.

These properties can be defined in a `sonar-project.properties` files, or as command line arguments when you start the Sonar Scanner :  
`sonar-scanner -Dsonar.lang.patterns.dockerfile=Dockerfile,some-dir/Dockerfile.* -Dsonar.hadolint.reports.path=results/report.xml`  

### Compatibility Matrix

|   plugin version   |    hadolint version     | supported SonarQube version |
|:------------------:|:-----------------------:|:---------------------------:|
|        1.0.0       |          1.18.x         |        7.9 -> 8.4           |
|        1.1.0       |          2.5.x          |           8.9.x             |

### Building the plugin
If you want to build the plugin :
- download the project code from GitHub
- open a terminal at the project's root
- simply use the following Maven command : `mvn clean package`
- plugin JAR file will then be available inside the target/ directory

### How to contribute
If you experienced a problem with the plugin please open an issue. Inside this issue please explain us how to reproduce this issue and paste the log. 

If you want to do a PR, please put inside of it the reason of this pull request. If this pull request fix an issue please insert the number of the issue or explain inside of the PR how to reproduce this issue.

All details are available in [CONTRIBUTING](https://github.com/cnescatlab/sonar-hadolint-plugin/CONTRIBUTING.md).

### Feedback and Support

Bugs and Feature requests: https://github.com/cnescatlab/sonar-hadolint-plugin/issues

### License
Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
