/*
 * Copyright (c) Seat24 AB
 */

package com.etraveli.oss.codestyle.projects.enforcer

import com.etraveli.oss.codestyle.projects.CommonProjectTypes
import com.etraveli.oss.codestyle.projects.ProjectType
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.project.MavenProject

/**
 * Enforcer rule to validate ProjectType compliance, to harmonize the pom structure in terms
 * of groupId, artifactId, packaging and (rudimentary) content checks.
 *
 * @param dontEvaluateGroupIds Ignore any dependencies whose groupIDs match any of the patterns supplied
 * @param permittedProjectTypes A List containing the ProjectTypes permitted.
 * Defaults to `CommonProjectTypes.values().asList()`.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 * @see ProjectType
 */
open class PermittedProjectTypeRule @JvmOverloads constructor(
  val dontEvaluateGroupIds: List<String>? = null,
  val permittedProjectTypes: List<ProjectType> = CommonProjectTypes.values()
    .asList()
) : AbstractNonCacheableEnforcerRule() {

  // Internal state
  private val partialDescription = permittedProjectTypes
    .mapIndexed { index, current -> "\n [$index/${permittedProjectTypes.size}]: $current" }
    .reduce { acc, s -> "$acc$s" }

  /**
   * Supplies the short rule description for this MavenEnforcerRule.
   */
  override fun getShortRuleDescription(): String =
    "POM groupId, artifactId and packaging must comply with defined project standard."

  /**
   * Delegate method, implemented by concrete subclasses.
   *
   * @param project The active MavenProject.
   * @param helper  The EnforcerRuleHelper instance, from which the MavenProject has been retrieved.
   * @throws RuleFailureException If the enforcer rule was not satisfied.
   */
  override fun performValidation(project: MavenProject, helper: EnforcerRuleHelper) {

    // Does any of the supplied project types match?
    val firstMatchingProjectType = permittedProjectTypes.firstOrNull {
      it.isCompliantWith(project, dontEvaluateGroupIds)
    }

    if (firstMatchingProjectType == null) {

      throw RuleFailureException(
        "None of the permitted ProjectTypes matched ${prettyPrint(project)}. " +
          "Permitted ProjectTypes:\n$partialDescription")

    } else if (helper.log.isDebugEnabled) {

      helper.log.debug("Found matching ProjectType [$firstMatchingProjectType] " +
                         "for project ${prettyPrint(project)}")
    }
  }

  override fun toString(): String {

    val ignoreDescription = when (dontEvaluateGroupIds.isNullOrEmpty()) {
      true -> "ignoring no artifacts."
      else -> {

        val nonEvaldGroupIDs = dontEvaluateGroupIds.map { it }
          .reduce { acc, s -> "$acc, $s" }

        "ignoring artifacts matching [${dontEvaluateGroupIds.size}] groupIDs: [$nonEvaldGroupIDs]"
      }
    }

    return "${this::class.java.simpleName} $ignoreDescription" +
      "\n[${permittedProjectTypes.size}] known project types: $partialDescription"
  }

  companion object {

    @JvmStatic
    internal fun prettyPrint(project: MavenProject): String =
      "GAV [${project.groupId}:${project.artifactId}:${project.packaging}]"
  }
}