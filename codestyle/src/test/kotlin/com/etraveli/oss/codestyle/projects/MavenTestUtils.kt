/*
 * Copyright (c) Seat24 AB
 */

package com.etraveli.oss.codestyle.projects

import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.apache.maven.plugin.testing.stubs.ArtifactStub
import org.apache.maven.project.MavenProject
import java.io.InputStreamReader

/**
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
object MavenTestUtils {

    /**
     * The default Stub version.
     */
    @JvmStatic
    private val DEFAULT_VERSION = "1.0.0"

    /**
     * Reads the POM found at the supplied filePath, retrieving the created MavenProject.
     *
     * @param filePath The path to the pom.xml file.
     * @return The MavenProject created from the filePath pom.
     */
    @JvmStatic
    fun readPom(filePath: String): MavenProject {

        try {
            val pomResource = MavenTestUtils::class.java
                .classLoader
                .getResource(filePath)

            val pomReader = InputStreamReader(pomResource.openStream())
            return MavenProject(MavenXpp3Reader().read(pomReader))

        } catch (e: Exception) {
            throw IllegalArgumentException("Could not read pom from [$filePath]", e)
        }
    }

    /**
     * Creates a MavenProject from the supplied data, and using version 1.0.0.
     *
     * @param packaging  The packaging for the MavenProject to return.
     * @param groupId    The groupId for the MavenProject to return.
     * @param artifactId The artifactId for the MavenProject to return.
     * @return a MavenProjectStub created from the supplied properties.
     */
    @JvmStatic
    fun getStub(packaging: String,
                groupId: String,
                artifactId: String): MavenProject {
        // Delegate
        return getStub(packaging, groupId, artifactId, DEFAULT_VERSION)
    }

    /**
     * Creates a MavenProject from the supplied data.
     *
     * @param packaging  The packaging for the MavenProject to return.
     * @param groupId    The groupId for the MavenProject to return.
     * @param artifactId The artifactId for the MavenProject to return.
     * @param version    The version for the MavenProject to return.
     * @return a MavenProjectStub created from the supplied properties.
     */
    @JvmStatic
    fun getStub(packaging: String,
                groupId: String,
                artifactId: String,
                version: String): MavenProject {

        val model = Model()
        model.modelVersion = "4.0.0"
        model.groupId = groupId
        model.artifactId = artifactId
        model.version = version
        model.packaging = packaging

        // The MavenProjectStub does not query its model for GAV values ...
        val toReturn = MavenProject(model)
        toReturn.groupId = groupId
        toReturn.artifactId = artifactId
        toReturn.packaging = packaging
        toReturn.version = version

        toReturn.artifact = ArtifactStub()
        toReturn.artifact.artifactId = artifactId
        toReturn.artifact.groupId = groupId

        return toReturn
    }
}