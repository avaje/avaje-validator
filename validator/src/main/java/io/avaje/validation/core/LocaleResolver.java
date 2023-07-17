package io.avaje.validation.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.avaje.lang.Nullable;

final class LocaleResolver {

  private final Locale defaultLocale;
  private final Set<Locale> otherLocales = new HashSet<>();

  LocaleResolver(Locale defaultLocale, Collection<Locale> others) {
    this.defaultLocale = defaultLocale;
    otherLocales.addAll(others);
  }

  public Locale defaultLocale() {
    return defaultLocale;
  }

  public Set<Locale> otherLocales() {
    return otherLocales;
  }

  public Locale resolve(@Nullable Locale requestLocale) {
    if (requestLocale == null || !otherLocales.contains(requestLocale)) {
      return defaultLocale;
    } else {
      return requestLocale;
    }
  }
}
