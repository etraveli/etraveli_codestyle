# About etraveli-codestyle-kotlin-bom

The Kotlin BOM is used to simplify including any of the rather 
large amounts of Kotlin dependencies within any project. All Kotlin
dependencies within this BOM use the same version constant variable, 
namely

    <kotlin.version>1.2.61</kotlin.version>
    
To use another version of Kotlin in your leaf project, simply override
this version constant with the desired Kotlin version.   

### Dependency Graph

The dependency graph for this project is shown below:

![Dependency Graph](./images/dependency_graph.png)