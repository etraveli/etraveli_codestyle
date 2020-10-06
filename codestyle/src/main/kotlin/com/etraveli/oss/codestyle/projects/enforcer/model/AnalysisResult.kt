/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects.enforcer.model

import com.etraveli.oss.codestyle.projects.ProjectType

/**
 * ## Project or artifact Analysis result holder
 *
 * Simple holder and supertype for concrete results.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
interface AnalysisResult {

  fun isExact(): Boolean
}

/**
 * Exact [ProjectType] match indication
 *
 * @param projectType The exact project type found.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
open class ExactResult(var projectType: ProjectType) : AnalysisResult {

  override fun isExact(): Boolean = true
}

/**
 * Fuzzy or inexact match indication
 *
 * @param closestProjectType if supplied, indicates the closest found [ProjectType]
 * @param message if supplied, indicates the error message when matching against a [ProjectType].
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
open class InexactResult @JvmOverloads constructor(
  var closestProjectType: ProjectType? = null,
  var message: String? = null) : AnalysisResult {
  override fun isExact(): Boolean = false
}