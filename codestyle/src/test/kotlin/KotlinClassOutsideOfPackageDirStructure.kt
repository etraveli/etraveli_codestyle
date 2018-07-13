/*
 * Copyright (c) Seat24 AB
 */

package com.etraveli.oss.codestyle.projects.enforcer

import java.io.Serializable

class KotlinClassOutsideOfPackageDirStructure(val foo : String = "foo!") : Serializable {

    override fun toString(): String = "This is a $foo"
}