package io.avaje.validation.generator;

import java.util.Collection;

final class TopPackage {

  private String topPackage;

  static String of(Collection<String> values) {
    return new TopPackage(values).value();
  }

  private String value() {
    return topPackage;
  }

  private TopPackage(Collection<String> values) {
    for (final String pkg : values) {
      topPackage = ProcessorUtils.commonParent(topPackage, pkg);
    }
  }
}
