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
}