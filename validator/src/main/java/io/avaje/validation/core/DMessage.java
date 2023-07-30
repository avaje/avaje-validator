package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationContext;

import java.util.Map;

record DMessage(String template, Map<String, Object> attributes, int dedupNumber)
    implements ValidationContext.Message {

  // templates can be the same across multiple adapters
  // these numbers ensure no cache collision
  private static int messageCounter = 0;

  DMessage(String template, Map<String, Object> attributes) {
    this(template, attributes, messageCounter++);
  }

  @Override
  public String lookupkey() {
    return template + dedupNumber;
  }
}
