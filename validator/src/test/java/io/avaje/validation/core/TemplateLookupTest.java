package io.avaje.validation.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class TemplateLookupTest {

  private final TemplateLookup lookup;

  TemplateLookupTest() {
    final var localeResolver = new LocaleResolver(Locale.ENGLISH, List.of(Locale.GERMAN));
    final var defaultResourceBundle =
        new ResourceBundleManager(List.of(), List.of(), localeResolver);
    this.lookup = new TemplateLookup(defaultResourceBundle);
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
