package io.avaje.validation.spi;

import java.util.Map;

/** Reads an Annotation's attributes and the message template and interpolates the message */
public non-sealed interface MessageInterpolator extends ValidationExtension {

  /**
   * Interpolate the given message with the annotation attributes
   *
   * @param template The template loaded from annotation/resourceBundle
   * @param attributes The Constraint annotation's attributes
   * @return The interpolated validation error message
   */
  String interpolate(String template, Map<String, Object> attributes);
}
