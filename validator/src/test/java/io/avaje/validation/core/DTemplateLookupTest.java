package io.avaje.validation.core;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DTemplateLookupTest {

  private final DTemplateLookup lookup;

  DTemplateLookupTest() {
    var localeResolver = new DLocaleResolver(Locale.ENGLISH, Locale.GERMAN);
    var defaultResourceBundle = new DResourceBundleManager("io.avaje.validation.Messages", localeResolver);
    this.lookup = new DTemplateLookup(defaultResourceBundle);
  }

  @Test
  void lookupKnownKey() {
    String key = "{avaje.AssertTrue.message}";
    assertThat(lookup.lookup(key, Locale.ENGLISH)).isEqualTo("must be true");
    assertThat(lookup.lookup(key, Locale.GERMAN)).isEqualTo("muss wahr sein");
  }

  @Test
  void lookupUnknownKey() {
    String key = "My literal msg";
    assertThat(lookup.lookup(key, Locale.ENGLISH)).isEqualTo("My literal msg");
    assertThat(lookup.lookup(key, Locale.GERMAN)).isEqualTo("My literal msg");
  }
}
