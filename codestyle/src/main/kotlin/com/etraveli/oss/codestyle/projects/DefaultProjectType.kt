/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects

/**
 * Default ProjectType implementation which uses a [Regex] to determine if the groupID, artifactID
 * and packaging matches required presets.
 *
 * @param groupIdRegex The [Regex] to identify matching Aether GroupIDs for this [ProjectType]
 * @param artifactIdRegex The [Regex] to identify matching Aether ArtifactIDs for this [ProjectType]
 * @param packagingRegex The [Regex] to identify matching Aether packaging for this [ProjectType]
 * @param acceptNullValues Indicates if received `null` values should be accepted or rejected.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
open class DefaultProjectType(

  /**
   * The [Regex] to identify matching Aether GroupIDs for this [ProjectType]
   */
  protected val groupIdRegex: Regex,

  /**
   * The [Regex] to identify matching Aether ArtifactIDs for this [ProjectType]
   */
  protected val artifactIdRegex: Regex,

  /**
   * The [Regex] to identify matching Aether packaging for this [ProjectType]
   */
  protected val packagingRegex: Regex,

  /**
   * Indicates if received [null]s should be accepted or rejected.
   */
  protected val acceptNullValues: Boolean = false) : ProjectType {

  /**
   * Convenience constructor using the pure String `Pattern`s instead of the full [Regex] objects.
   *
   * @see #getDefaultRegexFor
   * @see #IGNORE_CASE_AND_COMMENTS
   */
  @JvmOverloads
  constructor(groupIdPattern: String? = null,
              artifactIdPattern: String? = null,
              packagingPattern: String? = null,
              acceptNullValues: Boolean = false
  ) : this(ProjectType.getDefaultRegexFor(groupIdPattern),
           ProjectType.getDefaultRegexFor(artifactIdPattern),
           ProjectType.getDefaultRegexFor(packagingPattern),
           acceptNullValues)

  /**
   * Default implementation validates that the received [artifactID] matches the [artifactIdRegex]
   * or - if null - returns a value corresponding to the [acceptNullValues] constructor argument.
   *
   * @param artifactID The value of the `project.artifactID` element from a Maven POM.
   * @return `true` if the artifactID is compliant with this ProjectType.
   */
  override fun isCompliantArtifactID(artifactID: String?): Boolean = when (artifactID) {
    null -> acceptNullValues
    else -> artifactIdRegex.matches(artifactID)
  }

  /**
   * Default implementation validates that the received [groupID] matches the [groupIdRegex]
   * or - if null - returns a value corresponding to the [acceptNullValues] constructor argument.
   *
   * @param groupID The value of the `project.groupId` element from a Maven POM.
   * @return `true` if the groupID is compliant with this ProjectType.
   */
  override fun isCompliantGroupID(groupID: String?): Boolean = when (groupID) {
    null -> acceptNullValues
    else -> groupIdRegex.matches(groupID)
  }

  /**
   * Default implementation validates that the received [packaging] matches the [packagingRegex]
   * or - if null - returns a value corresponding to the [acceptNullValues] constructor argument.
   *
   * @param packaging The value of the `project.packaging` element from a Maven POM.
   * @return `true` if the packaging is compliant with this ProjectType.
   */
  override fun isCompliantPackaging(packaging: String?): Boolean = when (packaging) {
    null -> acceptNullValues
    else -> packagingRegex.matches(packaging)
  }

  /**
   * Packages a representation for this ProjectType, including its regular expressions.
   */
  override fun toString(): String {
    return "[ProjectType: ${javaClass.name}] - GroupIdRegex: ${groupIdRegex.pattern}, " +
      "ArtifactIdRegex: ${artifactIdRegex.pattern}, " +
      "PackagingRegex: ${packagingRegex.pattern}"
  }

  /**
   * Validates equality by comparing the [Regex] members of both [DefaultProjectType] objects.
   */
  override fun equals(other: Any?): Boolean {

    // Fail fast
    if (this === other) return true
    if (other !is DefaultProjectType) return false

    // Delegate to internal state
    if (groupIdRegex != other.groupIdRegex) return false
    if (artifactIdRegex != other.artifactIdRegex) return false
    if (packagingRegex != other.packagingRegex) return false
    if (acceptNullValues != other.acceptNullValues) return false

    return true
  }

  /**
   * Simply delegates the hashCode to the internal [Regex] objects and the [acceptNullValues].
   */
  override fun hashCode(): Int {
    var result = groupIdRegex.hashCode()
    result = 31 * result + artifactIdRegex.hashCode()
    result = 31 * result + packagingRegex.hashCode()
    result = 31 * result + acceptNullValues.hashCode()
    return result
  }
}