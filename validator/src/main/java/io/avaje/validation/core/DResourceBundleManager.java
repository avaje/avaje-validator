package io.avaje.validation.core;

import io.avaje.lang.Nullable;

import java.util.*;

final class DResourceBundleManager {


  private final Map<Locale, ResourceBundle> map = new HashMap<>();

  DResourceBundleManager(String name, LocaleResolver localeResolver) {
    map.put(localeResolver.defaultLocale(), bundle(name, localeResolver.defaultLocale()));
    for (Locale locale : localeResolver.otherLocales()) {
      map.put(locale, bundle(name, locale));
    }
  }

  private static ResourceBundle bundle(String name, Locale locale) {
    return ResourceBundle.getBundle(name, locale);
  }

  @Nullable
  public String message(String template, Locale resolvedLocale) {
    ResourceBundle bundle = map.get(resolvedLocale);
    boolean exists = bundle.containsKey(template);
    return !exists ? null : bundle.getString(template);
  }
}
