/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects.extractor

import java.io.File
import java.io.FileFilter

/**
 * Utility class containing constants and generic Pattern definitions.
 *
 * @author [Lennart J&ouml;relid](mailto:lennart.jorelid@etraveli.com)
 */
abstract class AbstractPackageExtractor : PackageExtractor {

  companion object {

    /**
     * The "package" reserved word.
     */
    const val PACKAGE_WORD = "package"

    /**
     * Retrieves the RegExp able to match a Package statement within a java, kotlin or C++ file.
     *
     * @param optionalSemicolonTermination if `true` the package statement may optionally be terminated by a
     * semicolon. Otherwise this termination is required.
     */
    @JvmStatic
    fun getPackageRegExp(optionalSemicolonTermination: Boolean) = Regex(
      "^\\s*${PACKAGE_WORD}\\s*([a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*?;" +
        when (optionalSemicolonTermination) {
          true -> "?"
          false -> ""
        } + "\\s*$");

    /**
     * Utility method which retrieves a FileFilter which accepts Files whose name ends
     * with the given suffix, case insensitive matching.
     */
    @JvmStatic
    fun getSuffixFileFilter(requiredLowerCaseSuffix: String) = FileFilter { aFile ->
      aFile != null &&
        aFile.isFile &&
        aFile.name.toLowerCase()
          .trim()
          .endsWith(requiredLowerCaseSuffix.toLowerCase())
    }

    /**
     * Default implementation to get a package for the
     */
    @JvmStatic
    protected fun getPackageFor(sourceFile: File, packageRegEx: Regex): String {

      for (aLine: String in sourceFile.readLines(Charsets.UTF_8)) {

        if (packageRegEx.matches(aLine)) {

          val lastIndexInLine = when (aLine.contains(";")) {
            true -> aLine.indexOfFirst { it == ';' }
            false -> aLine.length
          }

          // All Done.
          return aLine.trim()
            .substring(PACKAGE_WORD.length, lastIndexInLine)
            .trim()
        }
      }

      // No package statement found.
      // Return default package.
      return ""
    }
  }
}