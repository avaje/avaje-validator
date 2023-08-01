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
  void testNull() {
    assertThat(minBD.validate(null, request)).isTrue();
    assertThat(maxBD.validate(null, request)).isTrue();
    assertThat(minLong.validate(null, request)).isTrue();
    assertThat(maxLong.validate(null, request)).isTrue();
  }

  @Test
  void testMax() {
    assertThat(maxLong.validate(0, request)).isTrue();
    assertThat(maxFloat.validate(0f, request)).isTrue();
    assertThat(maxDouble.validate(0D, request)).isTrue();
    assertThat(maxLong.validate(0L, request)).isTrue();
    assertThat(maxLong.validate((short) 0, request)).isTrue();
    assertThat(maxLong.validate((byte) 0, request)).isTrue();
    assertThat(maxBI.validate(BigInteger.valueOf(0), request)).isTrue();
    assertThat(maxBD.validate(BigDecimal.valueOf(0), request)).isTrue();
  }

  @Test
  void testMin() {
    assertThat(minLong.validate(-0, request)).isTrue();
    assertThat(minFloat.validate(-0f, request)).isTrue();
    assertThat(minDouble.validate(-0D, request)).isTrue();
    assertThat(minLong.validate(-0L, request)).isTrue();
    assertThat(minLong.validate((short) -0, request)).isTrue();
    assertThat(minLong.validate((byte) -0, request)).isTrue();
    assertThat(minBI.validate(BigInteger.valueOf(-0), request)).isTrue();
    assertThat(minBD.validate(BigDecimal.valueOf(-0), request)).isTrue();
  }

  @Test
  void testMaxInValid() {
    assertThat(maxLong.validate(01234, request)).isFalse();
    assertThat(maxFloat.validate(01234f, request)).isFalse();
    assertThat(maxDouble.validate(01234D, request)).isFalse();
    assertThat(maxLong.validate(01234L, request)).isFalse();
    assertThat(maxLong.validate((short) 01234, request)).isFalse();
    assertThat(maxLong.validate((byte) 01234567, request)).isFalse();
    assertThat(maxBI.validate(BigInteger.valueOf(01234), request)).isFalse();
    assertThat(maxBD.validate(BigDecimal.valueOf(01234), request)).isFalse();
  }

  @Test
  void testMinInValid() {
    assertThat(minLong.validate(-01234, request)).isFalse();
    assertThat(minFloat.validate(-01234f, request)).isFalse();
    assertThat(minDouble.validate(-01234D, request)).isFalse();
    assertThat(minLong.validate(-01234L, request)).isFalse();
    assertThat(minLong.validate((short) -01234, request)).isFalse();
    assertThat(minLong.validate((byte) -01234567, request)).isFalse();
    assertThat(minBI.validate(BigInteger.valueOf(-01234), request)).isFalse();
    assertThat(minBD.validate(BigDecimal.valueOf(-01234), request)).isFalse();
  }
}
