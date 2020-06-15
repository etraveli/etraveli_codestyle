/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects.extractor

import java.io.File
import java.io.FileFilter

/**
 * Specification for extracting a package definition from the supplied sourceFile.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
interface PackageExtractor {

  /**
   * Retrieves a FileFilter which identifies the source files that can be handled by this PackageExtractor.
   *
   * @return a non-null FileFilter which identifies the source files that can be handled by this PackageExtractor.
   */
  val sourceFileFilter: FileFilter

  /**
   * Retrieves the package definition from the supplied sourceFile.
   *
   * @param sourceFile The sourceFile from which the package definition should be extracted.
   * @return The package of the sourceFile.
   */
  fun getPackage(sourceFile: File): String
}
