package io.avaje.validation.core;

import io.avaje.lang.Nullable;

import java.util.Locale;
import java.util.Set;

public interface LocaleResolver {
  Locale resolve(@Nullable Locale requestLocale);

  Locale defaultLocale();

  Set<Locale> otherLocales();
}
