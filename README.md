# [WIP] Hadolint SonarQube Plugin

SonarQube plugin for the Dockerfile analysis tool Hadolint.

SonarQube is an open platform to manage code quality. This plugin adds the ability to import pre-existing results of Hadolint in **checkstyle format only**.

This plugin is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

You can get Hadolint on GitHub : [hadolint](https://github.com/hadolint/hadolint).

### Known limitations

#### A Dockerfile has no file extension

SonarQube plugins can automatically identified whether they should run, thanks to the file extension.  
For example, a java plugin can easily identify the files it should analyze, with the .java extension.  
We can't use this behaviour for Dockerfiles to know if the plugin should run, because they have no standardized extension.  

As of now, we did not find a way to force SonarQube to automatically recognize a file without extension, so we have to use SonarQube plugin properties to :  
  
- explicitly tell if the plugin must run, to prevent it from running everytime, even when it's not needed  
- indicates where to find the files to analyze, so we can find the files we have to work on  

More details regarding properties are available in the dedicated section below.

#### A Dockerfile has no file extension (again)

To create this plugin, we had to define a "Dockerfile" language so that we can create rules linked to it.  
As we creates functions to parse Hadolint reports and create issues/metrics on Dockefiles, SonarQube is able to upload them and show the results on its web interface.  
The other side effect Dockerfiles ahaving no extension is that SonarQube is not able to identify the Dockerfiles from being coded in the "Dockerfile" language.  
They are linked to an "Unknown" language, and we did not find a workaround yet.  

#### Language, Metrics and Highlighting

The plugin creates a `Dockerfile` language inside SonarQube, linked to Hadolint rules.    
For identified Dockerfiles in your projects, the plugin calculates two metrics : number of comments, and number of line of code  
It also provides a basic text highlighting when you check the code on SonarQube web interface.  

### Quickstart
- Setup a SonarQube instance.
- Install Hadolint command line tool.
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
- `sonar.hadolint.activated`: Boolean indicating whether the plugin should run. Default: `false`.
- `sonar.hadolint.reports.path`: Comma separated list of path to Hadolint reports in checkstyle format. Default: `hadolint-report.xml`.
- `sonar.hadolint.dockerfiles.path`: Comma separated list of path to Dockerfiles analyzed by Hadolint. Default: `Dockerfile`.

### Compatibility Matrix

|   plugin version   |    hadolint version     | supported SonarQube version |
|:------------------:|:-----------------------:|:---------------------------:|
|        1.0.0       |          1.18.0         |        7.9 -> 8.4           |

### Building the plugin
If you want to build the plugin, open a terminal at the project's root and simply use the following Maven command : `mvn clean package`

### How to contribute
If you experienced a problem with the plugin please open an issue. Inside this issue please explain us how to reproduce this issue and paste the log. 

If you want to do a PR, please put inside of it the reason of this pull request. If this pull request fix an issue please insert the number of the issue or explain inside of the PR how to reproduce this issue.

All details are available in [CONTRIBUTING](https://github.com/lequal/sonar-hadolint-plugin/CONTRIBUTING.md).

### Feedback and Support

Bugs and Feature requests: https://github.com/lequal/sonar-hadolint-plugin/issues

### License
Licensed under the [GNU General Public License, Version 3.0](https://www.gnu.org/licenses/gpl.txt)
