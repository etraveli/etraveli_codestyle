package com.etraveli.oss.codestyle.projects.extractor

import java.io.File
import java.io.FileFilter

/**
 * [PackageExtractor] for Java source files.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
open class JavaPackageExtractor : AbstractPackageExtractor() {

  // Internal state
  private val packageRegEx = getPackageRegExp(false)

  override val sourceFileFilter: FileFilter
    get() = getSuffixFileFilter(".java")

  override fun getPackage(sourceFile: File): String = getPackageFor(sourceFile, packageRegEx)
}