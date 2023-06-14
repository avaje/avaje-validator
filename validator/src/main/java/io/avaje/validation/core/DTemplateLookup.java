package io.avaje.validation.core;

import java.util.Locale;

final class DTemplateLookup {
  private final DResourceBundleManager bundleManager;

  DTemplateLookup(DResourceBundleManager defaultBundle) {
    this.bundleManager = defaultBundle;
  }

  String lookup(String template, Locale resolvedLocale) {
    if (!isBundleKey(template)) {
      return template;
    }
    final String key = template.substring(1, template.length() - 1);
    final String msg = bundleManager.message(key, resolvedLocale);
    if (msg != null) {
      return msg;
    }
    return template;
  }

  private boolean isBundleKey(String template) {
    final int pos = template.indexOf('{');
    if (pos != 0) {
      return false;
    }
    // is it a bundle lookup?
    return template.charAt(template.length() - 1) == '}' && template.indexOf('{', 1) == -1;
  }
}
