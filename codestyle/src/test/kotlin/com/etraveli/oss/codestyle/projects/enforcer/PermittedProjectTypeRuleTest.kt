/*
 * Copyright (c) Seat24 AB
 */

package com.etraveli.oss.codestyle.projects.enforcer

import com.etraveli.oss.codestyle.projects.MavenTestUtils
import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.junit.Test

/**
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
class PermittedProjectTypeRuleTest {

    @Test
    fun validateCorrectPom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/tools-parent.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateTestProjectTypePom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/osgi-test-pom.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateAspectPom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/aspect-project.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test(expected = EnforcerRuleException::class)
    fun validateExceptionOnParentPomWithModules() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-parent-with-modules.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test(expected = EnforcerRuleException::class)
    fun validateExceptionOnBomWithDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-with-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule(
          listOf("^com\\.etraveli\\..*\\.generated\\..*", "^com\\.etraveli\\.oss\\.codestyle\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateNoExceptionOnBomHavingIgnoredDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/correct-bom-with-ignored-dependencies.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule(
          listOf("^com\\.etraveli\\..*\\.generated\\..*", "^com\\.etraveli\\.oss\\.codestyle\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }
}