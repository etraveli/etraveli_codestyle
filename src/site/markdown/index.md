# What is a "Codestyle" repo?

This repository does not contain artifacts intended for deployment into production.
Instead, this repository contains the `codestyle`, meaning that it contains a versioned 
and rule-set implementation for...

1. **Build reactor definitions**: the common build process, which means implementations of
   build rules for artifacts, plugin configurations and documentation.
2. **Component definitions**: the common module definition, which is used within 
   all normal build reactors within the system.
3. **Parent definitions**: the common parents for use within projects 
   producing artifacts.
   
A `Codestyle` repository, therefore, has no (or few) dependencies on other repositories.
The intended structure and responsibilities of projects/artifacts within repositories could/should
be ordered as illustrated in the image below:

![Code Style](img/repoStructure_readme.png "Repository Dependency Structure")   

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

The standard documentation is built using [Maven Site Plugin](https://maven.apache.org/plugins/maven-site-plugin/) 
mechanics - we use [Markdown](https://daringfireball.net/projects/markdown/syntax) with the added capabilities 
of [PlantUML](http://plantuml.com/) diagrams to render diagrams when needed.

This requires you to install a `dot` executable normally found within the [Graphviz](http://graphviz.org) open-source 
application. The section below provides installation instructions.

#### a. Graphviz installation and Dot path setup

To render the documentation graphics, you need to install `Graphviz` and place `dot` in your 
operating system path. In addition, you need to create a globally available Maven property called 
`path.to.dot` within your `$HOME/.m2/settings.xml` file. The property value should contain the 
path to the dot executable, similar to the snippet below: 

    <profiles>

        <!--
            Properties available for all builds.
            Purpose: Contain paths to locally installed applications, or URIs of a semi-secret nature.
        -->
        <profile>
            <id>injected_properties</id>
            <properties>
                <path.to.dot>/usr/bin/dot</path.to.dot>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>injected_properties</activeProfile>        
    </activeProfiles>

With this setup you should be able to properly build the Maven site documentation.

#### b. Building the Documentation

After graphviz/dot is installed, simply build the documentation using:

	mvn site
	
Build the staged documentation for all modules using:

	mvn site site:stage	
	
The staged documentation should land within the `/tmp/${reactor.name}/${version}` directory - typically something like
`/tmp/etraveli_codestyle/1.2.3/`. 
