package com.etraveli.oss.codestyle.projects

import java.lang.IllegalArgumentException

/**
 * Exception indicating that a [ProjectType] could not be resolved from a MavenProject or an Artifact.
 *
 * @param msg The human-readable error message, indicating the resolution failure cause.
 * @param bestEstimate The ProjectType which was the best estimate - and where some POM-based error
 * prevented the error to continue.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
open class FailedProjectResolutionException(
  msg : String,
  val bestEstimate : ProjectType) : IllegalArgumentException(msg)