package io.avaje.validation.core.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.core.BasicTest;

class MinMaxTest extends BasicTest {

  @interface Min {}

  @interface Max {}

  ValidationAdapter<Object> minBD =
      ctx.adapter(Min.class, Map.of("message", "mini", "value", -69L, "_type", "BigDecimal"));
  ValidationAdapter<Object> minBI =
    ctx.adapter(Min.class, Map.of("message", "mini", "value", -69L, "_type", "BigInteger"));

  ValidationAdapter<Object> minLong =
    ctx.adapter(Min.class, Map.of("message", "mini", "value", -69L, "_type", "Long"));
  ValidationAdapter<Object> minFloat =
    ctx.adapter(Min.class, Map.of("message", "mini", "value", -69L, "_type", "Float"));
  ValidationAdapter<Object> minDouble =
    ctx.adapter(Min.class, Map.of("message", "mini", "value", -69L, "_type", "Double"));

  ValidationAdapter<Object> maxBD =
      ctx.adapter(Max.class, Map.of("message", "maxwell", "value", 69L, "_type", "BigDecimal"));

  ValidationAdapter<Object> maxBI =
    ctx.adapter(Max.class, Map.of("message", "maxwell", "value", 69L, "_type", "BigInteger"));


  ValidationAdapter<Object> maxLong =
    ctx.adapter(Max.class, Map.of("message", "maxwell", "value", 69L, "_type", "Long"));

  ValidationAdapter<Object> maxFloat =
    ctx.adapter(Max.class, Map.of("message", "maxwell", "value", 69L, "_type", "Float"));

  ValidationAdapter<Object> maxDouble =
    ctx.adapter(Max.class, Map.of("message", "maxwell", "value", 69L, "_type", "Double"));

  @Test
  void continueOnInvalid_expect_false() {
    //BUG: This should really return true? - should continue validation !!
    assertThat(maxLong.validate(100L, request, "foo")).isFalse();
    assertThat(minLong.validate(-100L, request, "foo")).isFalse();
  }

  @Test
  void testNull() {
    assertThat(isValid(minBD, null)).isTrue();
    assertThat(isValid(maxBD, null)).isTrue();
    assertThat(isValid(minLong, null)).isTrue();
    assertThat(isValid(maxLong, null)).isTrue();
  }

  @Test
  void testMax() {
    assertThat(isValid(maxLong, 0)).isTrue();
    assertThat(isValid(maxFloat, 0f)).isTrue();
    assertThat(isValid(maxDouble, 0D)).isTrue();
    assertThat(isValid(maxLong, 0L)).isTrue();
    assertThat(isValid(maxLong, (short) 0)).isTrue();
    assertThat(isValid(maxLong, (byte) 0)).isTrue();
    assertThat(isValid(maxBI, BigInteger.valueOf(0))).isTrue();
    assertThat(isValid(maxBD, BigDecimal.valueOf(0))).isTrue();
  }

  @Test
  void testMin() {
    assertThat(isValid(minLong, -0)).isTrue();
    assertThat(isValid(minFloat, -0f)).isTrue();
    assertThat(isValid(minDouble, -0D)).isTrue();
    assertThat(isValid(minLong, -0L)).isTrue();
    assertThat(isValid(minLong, (short) -0)).isTrue();
    assertThat(isValid(minLong, (byte) -0)).isTrue();
    assertThat(isValid(minBI, BigInteger.valueOf(-0))).isTrue();
    assertThat(isValid(minBD, BigDecimal.valueOf(-0))).isTrue();
  }

  @Test
  void testMaxInValid() {
    assertThat(isValid(maxLong, 01234)).isFalse();
    assertThat(isValid(maxFloat, 01234f)).isFalse();
    assertThat(isValid(maxDouble, 01234D)).isFalse();
    assertThat(isValid(maxLong, 01234L)).isFalse();
    assertThat(isValid(maxLong, (short) 01234)).isFalse();
    assertThat(isValid(maxLong, (byte) 01234567)).isFalse();
    assertThat(isValid(maxBI, BigInteger.valueOf(01234))).isFalse();
    assertThat(isValid(maxBD, BigDecimal.valueOf(01234))).isFalse();
  }

  @Test
  void testMinInValid() {
    assertThat(isValid(minLong, -01234)).isFalse();
    assertThat(isValid(minFloat, -01234f)).isFalse();
    assertThat(isValid(minDouble, -01234D)).isFalse();
    assertThat(isValid(minLong, -01234L)).isFalse();
    assertThat(isValid(minLong, (short) -01234)).isFalse();
    assertThat(isValid(minLong, (byte) -01234567)).isFalse();
    assertThat(isValid(minBI, BigInteger.valueOf(-01234))).isFalse();
    assertThat(isValid(minBD, BigDecimal.valueOf(-01234))).isFalse();
  }
}
