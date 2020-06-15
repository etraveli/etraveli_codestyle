package com.etraveli.oss.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerLevel
import org.apache.maven.enforcer.rule.api.EnforcerRule

/**
 * AbstractEnforcerRule implementation which implements a non-cacheable behaviour.
 *
 * @param lvl Assigns the EnforcerLevel of this AbstractEnforcerRule.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
abstract class AbstractNonCacheableEnforcerRule(lvl: EnforcerLevel = EnforcerLevel.ERROR)
  : AbstractEnforcerRule(lvl) {

  /**
   * Always returns `null`.
   */
  override fun getCacheId(): String? = null

  /**
   * Always returns `false`.
   */
  override fun isResultValid(cachedRule: EnforcerRule): Boolean = false
}