/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.projects.extractor

import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException

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
     *
     * @param requiredLowerCaseSuffix the LC suffix required to be present for all files returned.
     */
    @JvmStatic
    fun getSuffixFileFilter(requiredLowerCaseSuffix: String) = FileFilter { aFile ->
      aFile != null
        && aFile.isFile
        && aFile.name.toLowerCase().trim().endsWith(requiredLowerCaseSuffix.toLowerCase())
    }

    /**
     * Default implementation to get a package from the supplied source file.
     *
     * @param sourceFile The source file to be introspected for a package.
     * @param packageRegEx a [Regex] which would identify a line containing the package definition.
     *
     * @return The package name.
     * @throws IllegalArgumentException if the sourceFile was a directory.
     */
    @JvmStatic
    protected fun getPackageFor(sourceFile: File, packageRegEx: Regex): String {

      if (sourceFile.isDirectory) {
        throw IllegalArgumentException("SourceFile [${sourceFile.canonicalPath}] should be a file, not a directory.")
      }

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