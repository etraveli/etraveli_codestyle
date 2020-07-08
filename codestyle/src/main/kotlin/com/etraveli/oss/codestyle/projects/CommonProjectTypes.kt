/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects

import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Dependency
import org.apache.maven.project.MavenProject
import java.util.regex.Pattern

/**
 * Commonly known and used ProjectTypes, collected within an enum.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
enum class CommonProjectTypes(artifactIdPattern: String?,
                              groupIdPattern: String?,
                              packagingPattern: String?,
                              acceptNullValues: Boolean = true) : ProjectType {

    /**
     * Reactor project, of type pom. May not contain anything except module definitions.
     */
    REACTOR(".*-reactor$", null, "pom", false),

    /**
     * Parent pom project, of type pom, defining dependencies and/or build
     * life cycles. May not contain module definitions.
     */
    PARENT(".*-parent$", null, "pom", false),

    /**
     * Bill-of-Materials project, of type pom, defining DependencyManagement entries.
     * May not contain module definitions.
     */
    BILL_OF_MATERIALS(".*-bom$", null, "pom", false),

    /**
     * Pom project, defining assemblies and/or aggregation projects. May not contain module definitions.
     */
    ASSEMBLY(".*-assembly$", null, "pom"),

    /**
     * Aspect definition project, holding publicly available aspect implementations.
     */
    ASPECT(".*-aspect$", ".*\\.aspect$", "bundle|jar", false),

    /**
     * Model project defining entities. May have test-scope dependencies on test and proof-of-concept projects.
     */
    MODEL(".*-model$", ".*\\.model$", "bundle|jar"),

    /**
     * Application project defining JEE-deployable artifacts.
     * Injections of implementation projects are permitted here.
     */
    JEE_APPLICATION(null, null, "war|ear|ejb", false),

    /**
     * Standalone application project defining runnable Java applications.
     * Injections of implementation projects are permitted here.
     */
    STANDALONE_APPLICATION(".*-application$", ".*\\.application$", "bundle|jar", false),

    /**
     * (Micro)Service project defining runnable Java applications.
     * Injections of implementation projects are permitted here.
     */
    MICROSERVICE(".*-service$", ".*\\.service$", "bundle|jar", false),

    /**
     * Example project providing runnable example code for showing the
     * typical scenarios of the component. Should contain relevant documentation
     * as well as cut-and-paste code. No dependency rules.
     */
    EXAMPLE(".*-example$", ".*\\.example$", null),

    /**
     * "javaagent" definition project, holding implementation of a
     * JVM agent to be launched in-process on the form
     * "-javaagent:[yourpath/][agentjar].jar=[option1]=[value1],[option2]=[value2]"
     *
     * This project type can import/inject implementation dependencies, as
     * it is considered an application entrypoint.
     */
    JAVA_AGENT(".*-agent$", ".*\\.agent$", "bundle|jar"),

    /**
     * API project, defining service interaction, abstract implementations and exceptions. May have compile-scope
     * dependencies on model projects within the same component, and test-scope dependencies on test and
     * proof-of-concept projects.
     */
    API(".*-api$", ".*\\.api$", "bundle|jar", false),

    /**
     * SPI project, defining service interaction, abstract implementations and exceptions. Must have compile-scope
     * dependencies to API projects within the same component. May have test-scope dependencies on test and
     * proof-of-concept projects.
     */
    SPI(".*-spi-\\w*$", ".*\\.spi\\.\\w*$", "bundle|jar", false),

    /**
     * Implementation project, implementing service interactions from an API or SPI project,
     * including dependencies on 3rd party libraries. Must have compile-scope dependencies to API or SPI projects
     * within the same component. May have test-scope dependencies on test and proof-of-concept projects.
     */
    IMPLEMENTATION(".*-impl-\\w*$", ".*\\.impl\\.\\w*$", "bundle|jar", false),

    /**
     * Test artifact helper project, implementing libraries facilitating testing within
     * other projects. No dependency rules.
     */
    TEST(".*-test$", ".*\\.test\\.\\w*$", null),

    /**
     * Integration test artifact helper project, used to perform automated
     * tests for several projects. No dependency rules.
     */
    INTEGRATION_TEST(".*-it$", ".*\\.it\\.\\w*$", null),

    /**
     * Codestyle helper project, providing implementations for use within the build definition cycle.
     * Typically used within local reactors to supply changes or augmentations to build configurations
     * such as `checkstyle.xml`, or custom enforcer rule implementations. No dependency rules.
     */
    CODESTYLE(".*-codestyle$", ".*\\.codestyle$", "jar|bundle"),

    /**
     * Project, defining a Maven plugin.
     */
    PLUGIN(".*-maven-plugin$", null, "maven-plugin"),

    /**
     * Proof-of-concept helper project, holding proof of concept implementations. No dependency rules.
     */
    PROOF_OF_CONCEPT(".*-poc$", ".*\\.poc\\.\\w*$", null);

    // Internal state
    private val delegate: DefaultProjectType = DefaultProjectType(
            groupIdPattern,
            artifactIdPattern,
            packagingPattern,
            acceptNullValues)

    override fun isCompliantArtifactID(artifactID: String?): Boolean = delegate.isCompliantArtifactID(artifactID)

    override fun isCompliantGroupID(groupID: String?): Boolean = delegate.isCompliantGroupID(groupID)

    override fun isCompliantPackaging(packaging: String?): Boolean = delegate.isCompliantPackaging(packaging)

    override fun toString(): String = "CommonProjectType.$name"

    /**
     * Special handling to separate PARENT, REACTOR and ASSEMBLY pom types.
     */
    override fun isCompliantWith(project: MavenProject, ignoredGroupIds : List<String>?): Boolean {

        // First, check standard compliance.
        val standardCompliance = super.isCompliantWith(project, ignoredGroupIds)

        // Define a helper function
        fun containsNonIgnoredElements(depList: List<Dependency>?): Boolean {

            if(depList == null || depList.isEmpty()) {
                return false;
            }

            // Any of the dependencies non-ignored?
            val ignoreRegExps = ignoredGroupIds?.map { ProjectType.getDefaultRegexFor(it) }?.toList()

            return when(ignoreRegExps == null) {
                true -> true
                else -> {

                    val foundAtLeastOneNonIgnoredMatch = depList.map {

                        val theGroupID = it.groupId.trim()

                        // Should we ignore the current dependency?
                        ignoreRegExps.any { currentIgnoreExp -> currentIgnoreExp.matches(theGroupID) }

                    }.any {

                        // Is there any non-ignored dependencies in the list?
                        !it
                    }

                    foundAtLeastOneNonIgnoredMatch
                }
            }
        }

        // All Done.
        return standardCompliance && when (this) {

            BILL_OF_MATERIALS -> {

                // This project can not contain Dependency definitions.
                val hasNoDependencies = !containsNonIgnoredElements(project.dependencies)

                // This project *should* contain DependencyManagement definitions.
                val hasDependencyManagementDefinitions = project.dependencyManagement != null
                        && containsNonIgnoredElements(project.dependencyManagement.dependencies)

                // All Done.
                hasNoDependencies && hasDependencyManagementDefinitions
            }

            REACTOR -> {

                // This project not contain Dependency definitions.
                val hasNoNonIgnoredDependencies = !containsNonIgnoredElements(project.dependencies)

                // This kind of project should not contain DependencyManagement definitions.
                val dependencyManagement = project.dependencyManagement
                val hasNoManagementDependencies = dependencyManagement == null
                        || !containsNonIgnoredElements(dependencyManagement.dependencies)

                // All Done.
                hasNoNonIgnoredDependencies && hasNoManagementDependencies
            }

            PARENT, ASSEMBLY -> {

                // This project should not contain modules.
                project.modules.isEmpty()
            }
            else -> true
        }
    }

    companion object {

        /**
         * Acquires the ProjectType instance for the provided internal Artifact, or throws an
         * IllegalArgumentException holding an exception message if a ProjectType could not be
         * found for the provided [Artifact].
         *
         * @param anArtifact The Maven Artifact for which a [CommonProjectTypes] object should be retrieved.
         * @return The [ProjectType] corresponding to the given [Artifact].
         * @throws IllegalArgumentException if [anArtifact] did not match any [CommonProjectTypes]
         */
        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun getProjectType(anArtifact: Artifact): CommonProjectTypes {

            val matches = CommonProjectTypes
              .values()
              .filter {
                  it.isCompliantArtifactID(anArtifact.artifactId) &&
                    it.isCompliantGroupID(anArtifact.groupId) &&
                    it.isCompliantPackaging(anArtifact.type)
              }

            val errorPrefix = "Incorrect Artifact type definition for [${anArtifact.groupId} :: " +
              "${anArtifact.artifactId} :: ${anArtifact.version} ]: "

            // Check sanity
            if (matches.isEmpty()) {
                throw IllegalArgumentException("$errorPrefix Not matching any CommonProjectTypes.")
            }
            if (matches.size > 1) {
                throw IllegalArgumentException("$errorPrefix Matching several project types ($matches).")
            }

            // All done.
            return matches[0]
        }

        /**
         * Acquires the ProjectType instance for the provided MavenProject,
         * or throws an IllegalArgumentException holding an exception message
         * if a ProjectType could not be found for the provided MavenProject.
         *
         * @param project The MavenProject to classify.
         * @return The corresponding ProjectType.
         * @throws IllegalArgumentException if the given project could not be mapped to a [single] ProjectType.
         * The exception message holds
         */
        @Throws(IllegalArgumentException::class)
        @JvmStatic
        @JvmOverloads
        fun getProjectType(project: MavenProject, ignorePatterns: List<String> = emptyList()): CommonProjectTypes {

            val matches = CommonProjectTypes
              .values()
              .filter {
                  it.isCompliantArtifactID(project.artifactId) &&
                    it.isCompliantGroupID(project.groupId) &&
                    it.isCompliantPackaging(project.packaging)
              }

            val errorPrefix = "Incorrect project type definition for [${project.groupId} " +
              ":: ${project.artifactId} :: ${project.version}]: "

            // Check sanity
            if (matches.isEmpty()) {
                throw IllegalArgumentException("$errorPrefix Not matching any CommonProjectTypes.")
            }
            if (matches.size > 1) {
                throw IllegalArgumentException("$errorPrefix Matching several project types ($matches).")
            }

            // Validate the internal requirements for the two different pom projects.
            val toReturn = matches[0]
            when (toReturn) {

                PARENT, ASSEMBLY ->

                    // This project should not contain modules.
                    if (project.modules != null && project.modules.isNotEmpty()) {
                        throw IllegalArgumentException("${toReturn.name} projects may not contain " +
                                                         "module definitions. (Modules are reserved for reactor projects).")
                    }

                BILL_OF_MATERIALS -> {

                    val errorText = "${toReturn.name} projects may not contain dependency definitions. " +
                      "(Dependencies should be defined within parent projects)."

                    fun containsNonIgnoredDependencies(depList: List<Dependency>?): Boolean {

                        if(depList == null || depList.isEmpty()) {
                            return false;
                        }

                        // Any of the dependencies non-ignored?
                        val compiledIgnorePatterns = ignorePatterns.map { Pattern.compile(it) }.toList()

                        return depList.map { aDependency ->
                            compiledIgnorePatterns.any { pattern -> pattern.matcher(aDependency.groupId).matches()
                            }
                        }.any { !it }
                    }

                    // This project not contain Dependency definitions.
                    if (containsNonIgnoredDependencies(project.dependencies)) {
                        throw IllegalArgumentException(errorText)
                    }
                }

                REACTOR -> {

                    val errorText = "${toReturn.name} projects may not contain " +
                      "dependency [incl. DependencyManagement] definitions. (Dependencies should be defined " +
                      "within parent projects)."

                    fun containsNonIgnoredElements(depList: List<Dependency>?): Boolean {

                        if(depList == null || depList.isEmpty()) {
                            return false;
                        }

                        // Any of the dependencies non-ignored?
                        val compiledIgnorePatterns = ignorePatterns.map { Pattern.compile(it) }.toList()

                        return depList.map { aDependency ->
                            compiledIgnorePatterns.any { pattern ->
                                pattern.matcher(aDependency.groupId)
                                  .matches()
                            }
                        }.any { !it }
                    }

                    // This project not contain Dependency definitions.
                    if (containsNonIgnoredElements(project.dependencies)) {
                        throw IllegalArgumentException(errorText)
                    }

                    // This kind of project should not contain DependencyManagement definitions.
                    val dependencyManagement = project.dependencyManagement
                    if (dependencyManagement != null && containsNonIgnoredElements(dependencyManagement.dependencies)) {
                        throw IllegalArgumentException(errorText)
                    }
                }

                else -> {

                    // Do nothing:
                    // No action should be taken for other project types.
                }
            }

            // All done.
            return toReturn
        }
    }
}