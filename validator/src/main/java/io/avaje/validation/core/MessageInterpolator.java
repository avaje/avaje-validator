package io.avaje.validation.core;

import java.util.Map;

public interface MessageInterpolator {

  String interpolate(String template, Map<String, Object> attributes);
}
