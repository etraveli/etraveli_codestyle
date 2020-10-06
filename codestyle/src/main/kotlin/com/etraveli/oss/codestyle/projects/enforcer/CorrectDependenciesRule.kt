/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects.enforcer

import com.etraveli.oss.codestyle.projects.CommonProjectTypes
import com.etraveli.oss.codestyle.projects.FailedProjectResolutionException
import com.etraveli.oss.codestyle.projects.ProjectType
import com.etraveli.oss.codestyle.projects.enforcer.model.AnalysisResult
import com.etraveli.oss.codestyle.projects.enforcer.model.InexactResult
import com.etraveli.oss.codestyle.projects.enforcer.model.ExactResult
import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.project.MavenProject
import java.lang.IllegalArgumentException

/**
 * Maven enforcement rule which ensures that Implementation [Artifact]s are not used as dependencies within
 * API, SPI or Model projects.
 *
 * This rule uses the [CommonProjectTypes] enumeration to identify some of the more common structures.
 *
 * @param ignoredProjectTypes List containing [ProjectType]s for which this rule should be ignored.
 * @param evaluateGroupIds List containing [Regex]ps which indicate which Maven GroupIDs should be included in this
 * Rule's evaluation.
 * @param dontEvaluateGroupIds List containing [Regex]p patterns which indicate which Maven GroupIDs should not be
 * included ("ignored") in this Rule's evaluation.
 * @param projectConverter A projectConverter method to convert each [MavenProject] to a [ProjectType].
 * Defaults to `CommonProjectTypes#getProjectType`.
 * @param artifactConverter A Maven [Artifact] to [ProjectType] converter function.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 * @see ProjectType
 * @see CommonProjectTypes
 */
open class CorrectDependenciesRule @JvmOverloads constructor(

  open val ignoredProjectTypes: List<ProjectType> = DEFAULT_IGNORED_PROJECTTYPES,

  private val evaluateGroupIds: List<String> = DEFAULT_EVALUATE_GROUPIDS,

  private val dontEvaluateGroupIds: List<String> = DEFAULT_DONT_EVALUATE_GROUPIDS,

  private val projectConverter: ((theProject: MavenProject) -> AnalysisResult?) = {
    defaultAnalysisOf(it, dontEvaluateGroupIds)
  },

  private val artifactConverter: ((theArtifact: Artifact) -> AnalysisResult?) = {
    defaultAnalysisOf(it)
  }) : AbstractNonCacheableEnforcerRule() {


  // Internal immutable state
  private val regexpsDontEvaluateGroupIDs = dontEvaluateGroupIds.map { Regex(it) }
  private val regexpsEvaluateGroupIDs = evaluateGroupIds.map { Regex(it) }

  override fun getShortRuleDescription(): String = "Incorrect Dependency found within project."

  override fun performValidation(project: MavenProject, helper: EnforcerRuleHelper) {

    // Don't evaluate if told not to.
    if (matches(project.groupId, regexpsDontEvaluateGroupIDs)) {

      // Log somewhat
      val log = helper.log

      if (log.isDebugEnabled) {
        log.debug("Ignored [" + project.groupId + ":" + project.artifactId +
                    "] since its groupId was excluded from enforcement.")
      }

      return
    }

    // Don't evaluate if not told to.
    val evaluationPatterns = regexpsEvaluateGroupIDs
    if (!matches(project.groupId, evaluationPatterns)) {

      // Log somewhat
      val log = helper.log

      if (log.isDebugEnabled) {
        log.debug("Ignored [" + project.groupId + ":" + project.artifactId +
                    "] since its groupId was not included in enforcement.")
      }
      return
    }

    // Acquire the ProjectType, and don't evaluate for ignored ProjectTypes.
    var foundProjectType : ProjectType? = null
    var errorMessage : String? = null

    when(val projectAnalysisResult = projectConverter.invoke(project)) {
      is ExactResult -> {
        foundProjectType = projectAnalysisResult.projectType
      }
      is InexactResult -> {
        foundProjectType = projectAnalysisResult.closestProjectType
        errorMessage = projectAnalysisResult.message
      }
      else -> {
        foundProjectType = null
      }
    }

    // Throw an exception if the
    if (foundProjectType != null && foundProjectType in ignoredProjectTypes) {
      return
    } else if(errorMessage != null) {
      throw throw IllegalArgumentException(errorMessage)
    }

    // Acquire all project dependencies.
    val artifactList = project.dependencyArtifacts
      ?: project.model.dependencies?.map {
        DefaultArtifact(it.groupId,
                        it.artifactId,
                        it.version,
                        it.scope,
                        it.type,
                        it.classifier,
                        DefaultArtifactHandler())
      } ?: emptyList()

    for (current in artifactList) {

      // Don't evaluate for test-scope dependencies.
      if (Artifact.SCOPE_TEST.equals(current.scope, ignoreCase = true)) {
        continue
      }

      // Should this Artifact be evaluated?
      val isIncludedInEvaluation = matches(current.groupId, regexpsEvaluateGroupIDs)
      val isNotExplicitlyExcludedFromEvaluation = !matches(current.groupId, regexpsDontEvaluateGroupIDs)
      if (isIncludedInEvaluation && isNotExplicitlyExcludedFromEvaluation) {

        var artifactProjectType : ProjectType? = null
        var artifactErrorMessage : String? = null

        when(val artifactAnalyzed = artifactConverter(current)) {
          is ExactResult -> {
            artifactProjectType = artifactAnalyzed.projectType
          }
          is InexactResult -> {
            artifactProjectType = artifactAnalyzed.closestProjectType
            artifactErrorMessage = artifactAnalyzed.message
          }
          else -> {
            artifactProjectType = null
          }
        }

        // Log somewhat
        val log = helper.log
        if(log.isInfoEnabled && artifactErrorMessage !== null) {
          log.info(artifactErrorMessage)
        }


        if(artifactProjectType != null) {
          val prefix = "Don't use $artifactProjectType dependencies "

          if (artifactProjectType === CommonProjectTypes.IMPLEMENTATION) {
            throw RuleFailureException(prefix + "within [${CommonProjectTypes.IMPLEMENTATION.name}] projects. " +
                                         "Implementation projects should only be imported in " +
                                         "provided/runtime/test scope", current)
          }

          if (artifactProjectType === CommonProjectTypes.TEST) {
            throw RuleFailureException(prefix + "in compile scope for non-test artifacts.", current)
          }

          if (artifactProjectType === CommonProjectTypes.JEE_APPLICATION
            || artifactProjectType === CommonProjectTypes.PROOF_OF_CONCEPT) {
            throw RuleFailureException(prefix + "in bundles.", current)
          }

          if (artifactProjectType === CommonProjectTypes.BILL_OF_MATERIALS) {
            throw RuleFailureException(prefix + "in Dependency block. (Use only as DependencyManagement " +
                                         "import-scoped dependencies).")
          }
        }
      }
    }
  }

  companion object {

    @JvmStatic
    internal fun defaultAnalysisOf(theProject: MavenProject,
                                   dontEvaluateGroupIdPatterns: List<String>) : AnalysisResult? {

      var toReturn :AnalysisResult? = null

      try {
        toReturn = ExactResult(CommonProjectTypes.getProjectType(theProject, dontEvaluateGroupIdPatterns))
      } catch (e: FailedProjectResolutionException) {
        toReturn = InexactResult(e.bestEstimate, e.message)
      } catch (e : Exception) {
        // Ignore this
      }

      return toReturn
    }

    @JvmStatic
    internal fun defaultAnalysisOf(anArtifact: Artifact) : AnalysisResult? {

      var toReturn :AnalysisResult? = null

      try {
        toReturn = ExactResult(CommonProjectTypes.getProjectType(anArtifact))
      } catch (e: FailedProjectResolutionException) {
        toReturn = InexactResult(e.bestEstimate, e.message)
      } catch (e : Exception) {
        // Ignore this
      }

      return toReturn
    }

    /**
     * List containing [Regex] patterns matching Maven GroupIDs to be excluded from this Rule's evaluation.
     */
    @JvmStatic
    val DEFAULT_DONT_EVALUATE_GROUPIDS = listOf("^com\\.etraveli\\..*\\.generated\\..*",
                                                "^com\\.etraveli\\.oss\\.codestyle\\..*")

    /**
     * List containing [Regex] patterns matching Maven GroupIDs to be included in this Rule's evaluation.
     */
    @JvmStatic
    val DEFAULT_EVALUATE_GROUPIDS = listOf("^com\\.etraveli\\..*");

    /**
     * List containing [ProjectType]s to be excluded from evaluation by default.
     */
    @JvmStatic
    val DEFAULT_IGNORED_PROJECTTYPES = listOf(
      CommonProjectTypes.JEE_APPLICATION,
      CommonProjectTypes.PARENT,
      CommonProjectTypes.ASSEMBLY,
      CommonProjectTypes.REACTOR,
      CommonProjectTypes.PROOF_OF_CONCEPT,
      CommonProjectTypes.EXAMPLE,
      CommonProjectTypes.TEST,
      CommonProjectTypes.JAVA_AGENT,
      CommonProjectTypes.STANDALONE_APPLICATION)
  }
}