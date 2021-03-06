package com.etraveli.oss.codestyle.projects.enforcer

import com.etraveli.oss.codestyle.projects.MavenTestUtils
import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.junit.Assert
import org.junit.Test

class CorrectDependenciesRuleTest {

    @Test(expected = EnforcerRuleException::class)
    fun validateCorrectPom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-as-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateExceptionOnBomImportedInDependencyScope() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-as-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule()

        // Act & Assert
        try {

            unitUnderTest.performValidation(project, mockHelper)

            Assert.fail("CorrectPackagingRule should yield an exception for projects not " +
                    "complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Validate that the message contains the correct failure reason.
            Assert.assertTrue(
                    message.contains("Don't use CommonProjectType.BILL_OF_MATERIALS dependencies in Dependency " +
                            "block. (Use only as DependencyManagement import-scoped dependencies)."))
        }
    }

    @Test
    fun validateNoExceptionOnBomHavingIgnoredDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/correct-bom-with-ignored-dependencies.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule(
          dontEvaluateGroupIds = listOf("^com\\.etraveli\\..*\\.generated\\..*", "^com\\.etraveli\\.oss\\.codestyle\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateOkApiPomHavingIgnoredDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/correct-api-with-ignored-dependencies.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validateExceptionOnBomHavingDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-with-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule(
          dontEvaluateGroupIds = listOf("^com\\.etraveli\\..*\\.generated\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test(expected = EnforcerRuleException::class)
    fun validateExceptionOnImplProjectHavingImplDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-impl-with-impl-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule(
          dontEvaluateGroupIds = listOf("^com\\.etraveli\\..*\\.generated\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test(expected = EnforcerRuleException::class)
    fun validateExceptionOnApiInImpl() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-impl-with-impl-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule(
          dontEvaluateGroupIds = listOf("^com\\.etraveli\\..*\\.generated\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }
}