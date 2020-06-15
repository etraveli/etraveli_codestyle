/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects.extractor

import java.io.File
import java.io.FileFilter

/**
 * [PackageExtractor] for Kotlin source files.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
open class KotlinPackageExtractor : AbstractPackageExtractor() {

  // Internal state
  private val packageRegEx = getPackageRegExp(true)

  override val sourceFileFilter: FileFilter
    get() = getSuffixFileFilter(".kt")

  override fun getPackage(sourceFile: File): String = getPackageFor(sourceFile, packageRegEx)
}
