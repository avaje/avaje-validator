package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class DTemplateLookupTest {

  private final DTemplateLookup lookup;

  DTemplateLookupTest() {
    final var localeResolver = new DLocaleResolver(Locale.ENGLISH, Locale.GERMAN);
    final var defaultResourceBundle = new DResourceBundleManager("io.avaje.validation.Messages", localeResolver);
    this.lookup = new DTemplateLookup(defaultResourceBundle);
  }

  @Test
  void lookupKnownKey() {
    final String key = "{avaje.AssertTrue.message}";
    assertThat(lookup.lookup(key, Locale.ENGLISH)).isEqualTo("must be true");
    assertThat(lookup.lookup(key, Locale.GERMAN)).isEqualTo("muss wahr sein");
  }

  @Test
  void lookupUnknownKey() {
    final String key = "My literal msg";
    assertThat(lookup.lookup(key, Locale.ENGLISH)).isEqualTo("My literal msg");
    assertThat(lookup.lookup(key, Locale.GERMAN)).isEqualTo("My literal msg");
  }
}
