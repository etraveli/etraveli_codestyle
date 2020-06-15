/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects

import org.apache.maven.project.MavenProject
import java.io.Serializable

/**
 * Specification for how to classify Maven projects originating from their GAV.
 * All implementations must supply a `toString()` method to supply usable debug logs
 * from some of the enforcer rules, such as `PermittedProjectTypeRule`.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
interface ProjectType : Serializable {

    /**
     * Checks if the provided artifactID complies with the naming standard
     * for this ProjectType.
     *
     * @param artifactID The artifactID which should be checked for compliance.
     * @return `true` if the provided artifactID was compliant with the naming rules for this ProjectType.
     */
    fun isCompliantArtifactID(artifactID: String?): Boolean

    /**
     * Checks if the provided groupID complies with the naming standard
     * for this ProjectType.
     *
     * @param groupID The groupID which should be checked for compliance.
     * @return `true` if the provided groupID was compliant with the naming rules for this ProjectType.
     */
    fun isCompliantGroupID(groupID: String?): Boolean

    /**
     * Checks if the provided packaging complies with the standard/requirements
     * of this ProjectType.
     *
     * @param packaging The packaging which should be checked for compliance.
     * @return `true` if the provided packaging was compliant with the rules for this ProjectType.
     */
    fun isCompliantPackaging(packaging: String?): Boolean

    /**
     * Convenience implementation used to test whether or not a [MavenProject] is compliance with this [ProjectType].
     * Override to provide extra mechanics for validation; it is recommended not to forget to invoke
     * [isCompliantWith] as defined within the [ProjectType] interface.
     *
     * @param project The MavenProject to ascertain compliance for this ProjectType.
     */
    fun isCompliantWith(project: MavenProject, ignoredGroupIds : List<String>? = null): Boolean {

        return isCompliantGroupID(project.groupId) &&
                isCompliantArtifactID(project.artifactId) &&
                isCompliantPackaging(project.packaging)
    }

    companion object {

        /**
         * The set of [RegexOption]s permitting comments, ignoring case and permitting Canonical Equivalence.
         */
        @JvmStatic
        val STANDARD_REGEX_OPTIONS = setOf(
          RegexOption.COMMENTS,
          RegexOption.IGNORE_CASE,
          RegexOption.CANON_EQ)

        /**
         * Convenience function to create a [Regex] from the supplied pattern and using
         * [STANDARD_REGEX_OPTIONS] for options.
         *
         * @param pattern The regex pattern
         */
        @JvmStatic
        fun getDefaultRegexFor(pattern: String?): Regex {

            // Use clear, not clever, code.
            // This could be written in shorter, but more difficult-to-read ways.
            val effectivePattern = when(pattern == null) {
                true -> ".*"
                else -> pattern
            }

            // All Done.
            return Regex(effectivePattern, STANDARD_REGEX_OPTIONS)
        }
    }
}