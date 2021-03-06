/*
 * Copyright (c) Seat24 AB
 */
package com.etraveli.oss.codestyle.annotations.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * jUnit group category interface, indicating tests which must be run
 * using the `CdiRunner` jUnit Runner.
 * 
 * @author <a href="mailto:lennart.jorelid@etraveli.com">Lennart J&ouml;relid</a>, etraveli AB
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresInjection {
}
