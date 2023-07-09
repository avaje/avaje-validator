package io.avaje.validation.spi;

import java.util.Map;

/** Reads an Annotation's attributes and the message template and interpolates the message */
public interface MessageInterpolator {

  /**
   * Interpolate the given message with the annotation attributes
   *
   * @param template template
   * @param attributes
   * @return the interpolated validation error message
   */
  String interpolate(String template, Map<String, Object> attributes);
}
