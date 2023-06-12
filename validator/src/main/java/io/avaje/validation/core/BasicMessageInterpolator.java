package io.avaje.validation.core;

import java.util.Map;

final class BasicMessageInterpolator implements MessageInterpolator {

  @Override
  public String interpolate(String template, Object value, Map<String, Object> attributes) {

    String result = template.replace("{validatedValue}", String.valueOf(value));

    for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
      // needs work here to improve functionality, support local specific value formatting eg
      // Duration Max
      result = result.replace('{' + entry.getKey() + '}', String.valueOf(entry.getValue()));
    }
    // return the message
    return result;
  }
}
