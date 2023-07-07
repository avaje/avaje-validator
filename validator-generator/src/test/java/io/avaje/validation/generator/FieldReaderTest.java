package io.avaje.validation.generator;

import org.junit.jupiter.api.Test;

import io.avaje.validation.generator.Util;

import static org.junit.jupiter.api.Assertions.*;

class FieldReaderTest {

  @Test
  void trimAnnotations() {
    assertEquals("java.lang.String", Util.trimAnnotations("java.lang.String"));
    assertEquals(
        "java.lang.String",
        Util.trimAnnotations("java.lang.@javax.validation.constraints.Email String"));
    assertEquals(
        "java.lang.String",
        Util.trimAnnotations(
            "java.lang.@javax.validation.constraints.NotNull,@javax.validation.constraints.Size(min=2,max=150) String"));
    assertEquals(
        "java.lang.String",
        Util.trimAnnotations(
            "java.lang.@javax.validation.constraints.Email,@javax.validation.constraints.Size(max=100,\"amogus ))),a,.%\") String"));
  }
}
