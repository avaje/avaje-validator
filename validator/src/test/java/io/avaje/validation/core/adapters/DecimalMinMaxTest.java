package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class DecimalMinMaxTest extends BasicTest {

  @interface DecimalMin {}

  @interface DecimalMax {}

  ValidationAdapter<Object> minAdapter =
      ctx.adapter(DecimalMin.class, Map.of("message", "mini", "value", "-69", "_type", "Number"));

  ValidationAdapter<Object> maxAdapter =
      ctx.adapter(DecimalMax.class, Map.of("message", "maxwell", "value", "69", "_type", "Number"));

  @Test
  void continueOnInvalid_expect_false() {
    assertThat(minAdapter.validate(BigDecimal.valueOf(-100), request, "foo")).isTrue();
    assertThat(maxAdapter.validate(BigDecimal.valueOf(100), request, "foo")).isTrue();
  }

  @Test
  void testNull() {
    assertThat(isValid(minAdapter, null)).isTrue();
    assertThat(isValid(maxAdapter, null)).isTrue();
  }

  @Test
  void testMax() {

    assertThat(isValid(maxAdapter, 0)).isTrue();
    assertThat(isValid(maxAdapter, 0f)).isTrue();
    assertThat(isValid(maxAdapter, 0D)).isTrue();
    assertThat(isValid(maxAdapter, 0L)).isTrue();
    assertThat(isValid(maxAdapter, (short) 0)).isTrue();
    assertThat(isValid(maxAdapter, (byte) 0)).isTrue();
    assertThat(isValid(maxAdapter, BigInteger.valueOf(0))).isTrue();
    assertThat(isValid(maxAdapter, BigDecimal.valueOf(0))).isTrue();
  }

  @Test
  void testMin() {
    assertThat(isValid(minAdapter, -0)).isTrue();
    assertThat(isValid(minAdapter, -0f)).isTrue();
    assertThat(isValid(minAdapter, -0D)).isTrue();
    assertThat(isValid(minAdapter, -0L)).isTrue();
    assertThat(isValid(minAdapter, (short) -0)).isTrue();
    assertThat(isValid(minAdapter, (byte) -0)).isTrue();
    assertThat(isValid(minAdapter, BigInteger.valueOf(-0))).isTrue();
    assertThat(isValid(minAdapter, BigDecimal.valueOf(-0))).isTrue();
  }

  @Test
  void testMaxInValid() {
    assertThat(isValid(maxAdapter, 01234)).isFalse();
    assertThat(isValid(maxAdapter, 01234f)).isFalse();
    assertThat(isValid(maxAdapter, 01234D)).isFalse();
    assertThat(isValid(maxAdapter, 01234L)).isFalse();
    assertThat(isValid(maxAdapter, (short) 01234)).isFalse();
    assertThat(isValid(maxAdapter, (byte) 01234567)).isFalse();
    assertThat(isValid(maxAdapter, BigInteger.valueOf(01234))).isFalse();
    assertThat(isValid(maxAdapter, BigDecimal.valueOf(01234))).isFalse();
  }

  @Test
  void testMinInValid() {
    assertThat(isValid(minAdapter, -01234)).isFalse();
    assertThat(isValid(minAdapter, -01234f)).isFalse();
    assertThat(isValid(minAdapter, -01234D)).isFalse();
    assertThat(isValid(minAdapter, -01234L)).isFalse();
    assertThat(isValid(minAdapter, (short) -01234)).isFalse();
    assertThat(isValid(minAdapter, (byte) -01234567)).isFalse();
    assertThat(isValid(minAdapter, BigInteger.valueOf(-01234))).isFalse();
    assertThat(isValid(minAdapter, BigDecimal.valueOf(-01234))).isFalse();
  }
}
