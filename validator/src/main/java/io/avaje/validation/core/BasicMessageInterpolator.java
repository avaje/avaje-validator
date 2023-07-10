package io.avaje.validation.core;

import java.util.Map;

import io.avaje.validation.spi.MessageInterpolator;

final class BasicMessageInterpolator implements MessageInterpolator {

  @Override
  public String interpolate(String template, Map<String, Object> attributes) {
    String result = template;
    for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
      // needs work here to improve functionality, support local specific value formatting eg
      // Duration Max
      result = result.replace('{' + entry.getKey() + '}', String.valueOf(entry.getValue()));
    }
    // return the message
    return result;
  }
}
