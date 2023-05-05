package io.avaje.validation.core;

import java.util.Locale;

final class DTemplateLookup {
  private final DResourceBundleManager defaultBundle;

  DTemplateLookup(DResourceBundleManager defaultBundle) {
    this.defaultBundle = defaultBundle;
  }

  String lookup(String template, Locale resolvedLocale) {
    if (!isBundleKey(template)) {
      return template;
    }
    String key = template.substring(1, template.length() - 1);
    // ??? update algorithm to recursively search bundles ??? Probably not ...
    // todo: read user supplied override bundles first
    // todo: read contributor bundles second
    // read default bundle last
    String msg = defaultBundle.message(key, resolvedLocale);
    if (msg != null) {
      return msg;
    }
    return template;
  }

  private boolean isBundleKey(String template) {
    int pos = template.indexOf('{');
    if (pos != 0) {
      return false;
    }
    // is it a bundle lookup?
    return template.charAt(template.length() - 1) == '}' && template.indexOf('{', 1) == -1;
  }
}
