package io.avaje.validation.core;

import io.avaje.lang.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

final class DLocaleResolver implements LocaleResolver {

  private final Locale defaultLocale;
  private final Set<Locale> otherLocales = new HashSet<>();

  DLocaleResolver(Locale defaultLocale, Locale... others) {
    this.defaultLocale = defaultLocale;
    Collections.addAll(otherLocales, others);
  }

  @Override
  public Locale defaultLocale() {
    return defaultLocale;
  }

  @Override
  public Set<Locale> otherLocales() {
    return otherLocales;
  }

  @Override
  public Locale resolve(@Nullable Locale requestLocale) {
    if (requestLocale == null || !otherLocales.contains(requestLocale)) {
      return defaultLocale;
    } else {
      return requestLocale;
    }
  }
}
