package io.avaje.validation.core;

import static java.util.ResourceBundle.getBundle;

import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import io.avaje.applog.AppLog;
import io.avaje.lang.Nullable;

final class ResourceBundleManager {
  private static final System.Logger logger =
      AppLog.getLogger(ResourceBundleManager.class);
  private final Map<Locale, List<ResourceBundle>> map = new HashMap<>();
  private static final List<ResourceBundle> EMPTY = List.of();
  private static final String DEFAULT_BUNDLE = "io.avaje.validation.Messages";

  ResourceBundleManager(List<String> names, List<ResourceBundle> providedBundles, LocaleResolver localeResolver) {
    for (final var name : names) {
      addBundle(name, localeResolver.defaultLocale());
      for (final Locale locale : localeResolver.otherLocales()) {
        addBundle(name, locale);
      }
    }

    for (final var bundle : providedBundles) {
      map.computeIfAbsent(bundle.getLocale(), key -> new ArrayList<>()).add(bundle);
    }
    // since default is added last, it will be the last place messages will be resolved
    addBundle(DEFAULT_BUNDLE, localeResolver.defaultLocale());

    for (final Locale locale : localeResolver.otherLocales()) {
      addBundle(DEFAULT_BUNDLE, locale);
    }
  }

  private void addBundle(final String name, final Locale locale) {

    try {
      map.computeIfAbsent(locale, key -> new ArrayList<>()).add(getBundle(name, locale));

    } catch (MissingResourceException e) {
      logger.log(Level.ERROR, "failed to load " + name + " with locale " + locale);
    }
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
