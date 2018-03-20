/*
 * Copyright (c) Etraveli AB
 */

package com.etraveli.oss.codestyle.annotations.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * jUnit group category interface, indicating tests which should
 * be categorized as {@code integration} tests, i.e. not being run in isolation.
 *
 * @author <a href="mailto:lennart.jorelid@etraveli.com">Lennart J&ouml;relid</a>, etraveli AB
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}
