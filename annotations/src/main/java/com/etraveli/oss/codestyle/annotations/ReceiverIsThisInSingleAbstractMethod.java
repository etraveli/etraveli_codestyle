/*
 * Copyright (c) Seat24 AB
 */

package com.etraveli.oss.codestyle.annotations;

/**
 * Add this annotation to a class to indicate to the Kotlin compiler that the
 * sam-with-receiver compiler module should be used.
 *
 * The sam-with-receiver compiler plugin makes the first parameter of the annotated Java "single abstract method"
 * (SAM) interface method a receiver in Kotlin. This conversion only works when the SAM interface is passed as a
 * Kotlin lambda, both for SAM adapters and SAM constructors.
 *
 * @author <a href="mailto:lennart.jorelid@etraveli.com">Lennart J&ouml;relid</a>, etraveli AB
 */
public @interface ReceiverIsThisInSingleAbstractMethod {
}
