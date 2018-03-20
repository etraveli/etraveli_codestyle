# What is a "Codestyle" repo?

This repository does not contain artifacts intended for deployment into production.
Instead, this repository contains the `codestyle`, meaning that it contains a versioned 
and rule-set implementation for...

1. **Build reactor definitions**: the common build process, which means implementations of build rules for 
   artifacts, plugin configurations and documentation.
2. **Component definitions**: the common module definition, which is used within all normal build reactors 
   within the system.
3. **Parent definitions**: the common parents for use within projects producing artifacts.
   
### Intermission: The Code Style factor

It is important to code in style; strive to bring a smile to your face:
 
![Code Style](img/styleCode.jpg "Code in Style...")

## Using the "Codestyle" repo artifacts

The artifacts produced within the Codestyle repository should be used as parents within 
your repositories and artifact projects. Typically, the usage is limited to `parent` 
elements within build reactors, on the following form:

For `Java` artifact projects:

	<!--
    ################################################
    # Section 1:  Project information
    ################################################
    -->
    <parent>
        <groupId>com.etraveli.codestyle.poms.java</groupId>
        <artifactId>etraveli-codestyle-api-parent</artifactId>
        <version>5.2.1</version>
    </parent>
    
For `Kotlin` artifact projects:

	<!--
    ################################################
    # Section 1:  Project information
    ################################################
    -->
    <parent>
        <groupId>com.etraveli.codestyle.poms.kotlin</groupId>
        <artifactId>etraveli-codestyle-api-parent</artifactId>
        <version>5.2.1</version>
    </parent>

... and so on. Each of these parents contain a well-defined    

## Building the "Codestyle" repo

The codestyle repository is built using [Maven](http://maven.apache.org/) and Java 8+.
As with all codestyle-compliant repositories, you should be able to build its artifacts
using a standard Maven build:

	mvn clean install
	
## Building the Documentation

The standard documentation is built using 
[Maven Site Plugin](https://maven.apache.org/plugins/maven-site-plugin/) mechanics - we
use [Markdown](https://daringfireball.net/projects/markdown/syntax) with the added 
capabilities of [PlantUML](http://plantuml.com/) diagrams to render diagrams when needed.
Build the documentation using:

	mvn site
	
Build the staged documentation for all modules using:

	mvn site site:stage	


## How should I proceed?

Want to get started with your Codestyle-derived project?

1. Check out the [concepts](concepts.html).

1. Read through the thoughts and structure of [Software Components](nazgul_tools.html).

1. Read [a brief overview of the Codestyle reactor](nazgul_tools.html).

... and we are working on a few screencasts, to shorten the startup time even more...