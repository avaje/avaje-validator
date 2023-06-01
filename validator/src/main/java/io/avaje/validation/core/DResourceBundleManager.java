package io.avaje.validation.core;

import static java.util.ResourceBundle.getBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import io.avaje.lang.Nullable;

final class DResourceBundleManager {

  private final Map<Locale, List<ResourceBundle>> map = new HashMap<>();
  private static final List<ResourceBundle> EMPTY = List.of();
  private static final String DEFAULT_BUNDLE = "io.avaje.validation.Messages";

  DResourceBundleManager(
      List<String> names, List<ResourceBundle> providedBundles, LocaleResolver localeResolver) {

    for (final var name : names) {

      addBundle(name, localeResolver.defaultLocale());

      for (final Locale locale : localeResolver.otherLocales()) {

        addBundle(name, locale);
      }
    }

    for (final var bundle : providedBundles) {
      map.compute(
          bundle.getLocale(),
          (local, list) -> {
            if (list == null) list = new ArrayList<>();

            list.add(bundle);
            return list;
          });
    }
    // since default is added last, it will be the last place messages will be resolved
    addBundle(DEFAULT_BUNDLE, localeResolver.defaultLocale());

    for (final Locale locale : localeResolver.otherLocales()) {
      addBundle(DEFAULT_BUNDLE, locale);
    }
  }

  private void addBundle(final String name, final Locale locale) {
    map.compute(
        locale,
        (local, list) -> {
          if (list == null) list = new ArrayList<>();
          list.add(getBundle(name, local));
          return list;
        });
  }

  @Nullable
  public String message(String template, Locale resolvedLocale) {

    for (final var bundle : map.getOrDefault(resolvedLocale, EMPTY)) {

      if (bundle.containsKey(template)) {
        return bundle.getString(template);
      }
    }

    return null;
  }
}
