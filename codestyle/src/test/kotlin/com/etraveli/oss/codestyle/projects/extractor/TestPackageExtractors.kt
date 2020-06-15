/*
 * Copyright (c) Seat24 AB
 */

package com.etraveli.oss.codestyle.projects.enforcer.com.etraveli.oss.codestyle.projects.extractor

import com.etraveli.oss.codestyle.projects.extractor.PackageExtractor
import java.io.File
import java.io.FileFilter

/**
 * An incorrect PackageExtractor implementation, which does not have a default constructor.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
class IncorrectNoDefaultConstructorPackageExtractor(someConstructorArgument: String) : PackageExtractor {

    override val sourceFileFilter: FileFilter = FileFilter { true }

    override fun getPackage(sourceFile: File): String = "unimportant"
}

/**
 * A silly - but correctly implemented PackageExtractor, having a default constructor.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
class SillyPackageExtractor : PackageExtractor {

    override val sourceFileFilter: FileFilter = FileFilter { aFile -> aFile.isFile }

    override fun getPackage(sourceFile: File): String = "silly"
}