package io.avaje.validation.spi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Registers a type extending {@link io.avaje.validation.spi.ValidatorCustomizer
 * ValidatorCustomizer} to {@code META-INF/services}, annotated types will have an entry added to
 * {@code META-INF/services/io.avaje.validation.adapter.ValidatorCustomizer}
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface BuilderCustomizer {}
