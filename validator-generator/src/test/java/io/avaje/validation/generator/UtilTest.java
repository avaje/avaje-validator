package io.avaje.validation.generator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

  @Disabled
  @Test
  void baseTypeOfAdapter() {
    assertEquals("com.My", Util.baseTypeOfAdapter("com.jsonb.MyJsonAdapter"));
    assertEquals("com.foo.My", Util.baseTypeOfAdapter("com.foo.jsonb.MyJsonAdapter"));
    assertEquals("com.foo.bar.SomeType", Util.baseTypeOfAdapter("com.foo.bar.jsonb.SomeTypeJsonAdapter"));
    assertEquals("SomeType", Util.baseTypeOfAdapter("jsonb.SomeTypeJsonAdapter"));
  }

  @Test
  void initCap() {
    assertEquals("Hello", Util.initCap("hello"));
    assertEquals("Url", Util.initCap("url"));
    assertEquals("Fo", Util.initCap("fo"));
    assertEquals("A", Util.initCap("a"));
    assertEquals("InitCap", Util.initCap("initCap"));
  }

  @Test
  void initLower() {
    assertEquals("hello", Util.initLower("hello"));
    assertEquals("url", Util.initLower("URL"));
    assertEquals("initCap", Util.initLower("InitCap"));
  }

  @Test
  void validImportType_expect_false() {
    assertFalse(Util.validImportType("int", "org.foo"));
    assertFalse(Util.validImportType("java.lang.Integer", "org.foo"));
    assertFalse(Util.validImportType("org.foo.Bar", "org.foo"));
  }

  @Test
  void validImportType_expect_true() {
    assertTrue(Util.validImportType("java.lang.something.Foo", "org.foo"));
    assertTrue(Util.validImportType("org.foo.some.Bar", "org.foo"));
    assertTrue(Util.validImportType("org.other.Bar", "org.foo"));
  }
}
