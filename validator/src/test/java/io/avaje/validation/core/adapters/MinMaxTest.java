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

  ValidationAdapter<Object> minAdapter =
      ctx.adapter(Min.class, Map.of("message", "mini", "value", -69));

  ValidationAdapter<Object> maxAdapter =
      ctx.adapter(Max.class, Map.of("message", "maxwell", "value", 69));

  @Test
  void testNull() {
    assertThat(minAdapter.validate(null, request)).isTrue();
    assertThat(maxAdapter.validate(null, request)).isTrue();
  }

  @Test
  void testMax() {

    assertThat(maxAdapter.validate(0, request)).isTrue();
    assertThat(maxAdapter.validate(0f, request)).isTrue();
    assertThat(maxAdapter.validate(0D, request)).isTrue();
    assertThat(maxAdapter.validate(0L, request)).isTrue();
    assertThat(maxAdapter.validate((short) 0, request)).isTrue();
    assertThat(maxAdapter.validate((byte) 0, request)).isTrue();
    assertThat(maxAdapter.validate(BigInteger.valueOf(0), request)).isTrue();
    assertThat(maxAdapter.validate(BigDecimal.valueOf(0), request)).isTrue();
  }

  @Test
  void testMin() {
    assertThat(minAdapter.validate(-0, request)).isTrue();
    assertThat(minAdapter.validate(-0f, request)).isTrue();
    assertThat(minAdapter.validate(-0D, request)).isTrue();
    assertThat(minAdapter.validate(-0L, request)).isTrue();
    assertThat(minAdapter.validate((short) -0, request)).isTrue();
    assertThat(minAdapter.validate((byte) -0, request)).isTrue();
    assertThat(minAdapter.validate(BigInteger.valueOf(-0), request)).isTrue();
    assertThat(minAdapter.validate(BigDecimal.valueOf(-0), request)).isTrue();
  }

  @Test
  void testMaxInValid() {
    assertThat(maxAdapter.validate(01234, request)).isFalse();
    assertThat(maxAdapter.validate(01234f, request)).isFalse();
    assertThat(maxAdapter.validate(01234D, request)).isFalse();
    assertThat(maxAdapter.validate(01234L, request)).isFalse();
    assertThat(maxAdapter.validate((short) 01234, request)).isFalse();
    assertThat(maxAdapter.validate((byte) 01234567, request)).isFalse();
    assertThat(maxAdapter.validate(BigInteger.valueOf(01234), request)).isFalse();
    assertThat(maxAdapter.validate(BigDecimal.valueOf(01234), request)).isFalse();
  }

  @Test
  void testMinInValid() {
    assertThat(minAdapter.validate(-01234, request)).isFalse();
    assertThat(minAdapter.validate(-01234f, request)).isFalse();
    assertThat(minAdapter.validate(-01234D, request)).isFalse();
    assertThat(minAdapter.validate(-01234L, request)).isFalse();
    assertThat(minAdapter.validate((short) -01234, request)).isFalse();
    assertThat(minAdapter.validate((byte) -01234567, request)).isFalse();
    assertThat(minAdapter.validate(BigInteger.valueOf(-01234), request)).isFalse();
    assertThat(minAdapter.validate(BigDecimal.valueOf(-01234), request)).isFalse();
  }
}
