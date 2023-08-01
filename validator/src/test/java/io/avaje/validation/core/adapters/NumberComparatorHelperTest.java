package io.avaje.validation.core.adapters;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

class NumberComparatorHelperTest {

  @Test
  void asInteger() {
    assertEquals(-1, NumberComparatorHelper.compareDecimal("Integer", 9, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(-1, NumberComparatorHelper.compareDecimal("Integer", 9, BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("Integer", 10, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("Integer", 10, BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("Integer", 11, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("Integer", 11, BigDecimal.TEN, OptionalInt.of(1)));
  }

  @Test
  void asFloat() {
    assertEquals(-1, NumberComparatorHelper.compareDecimal("Float", 9.9F, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(-1, NumberComparatorHelper.compareDecimal("Float", 9.9F, BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("Float", 10.0F, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("Float", 10.0F, BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("Float", 10.1F, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("Float", 10.1F, BigDecimal.TEN, OptionalInt.of(1)));
  }

  @Test
  void asDouble() {
    assertEquals(-1, NumberComparatorHelper.compareDecimal("Double", 9.9D, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(-1, NumberComparatorHelper.compareDecimal("Double", 9.9D, BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("Double", 10.0D, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("Double", 10.0D, BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("Double", 10.1D, BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("Double", 10.1D, BigDecimal.TEN, OptionalInt.of(1)));
  }

  @Test
  void bigDecimal() {
    assertEquals(-1, NumberComparatorHelper.compareDecimal("BigDecimal", new BigDecimal("9.9"), BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(-1, NumberComparatorHelper.compareDecimal("BigDecimal", new BigDecimal("9.9"), BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("BigDecimal", new BigDecimal("10.0"), BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("BigDecimal", new BigDecimal("10.0"), BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("BigDecimal", new BigDecimal("10.1"), BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("BigDecimal", new BigDecimal("10.1"), BigDecimal.TEN, OptionalInt.of(1)));
  }

  @Test
  void bigInteger() {
    assertEquals(-1, NumberComparatorHelper.compareDecimal("BigInteger", new BigInteger("9"), BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(-1, NumberComparatorHelper.compareDecimal("BigInteger", new BigInteger("9"), BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("BigInteger", new BigInteger("10"), BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(0, NumberComparatorHelper.compareDecimal("BigInteger", new BigInteger("10"), BigDecimal.TEN, OptionalInt.of(1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("BigInteger", new BigInteger("11"), BigDecimal.TEN, OptionalInt.of(-1)));
    assertEquals(1, NumberComparatorHelper.compareDecimal("BigInteger", new BigInteger("11"), BigDecimal.TEN, OptionalInt.of(1)));
  }

}
