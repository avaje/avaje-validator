package io.avaje.validation.core;

import io.avaje.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class AllSortsDefaultMessageTest extends BaseAllSortsTest {

  @Test
  void notNull_EN() {
    var bean = new AllSortsBean();
    bean.myNotNull = null;
    ConstraintViolation constraint = one(bean, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must not be null");
  }

  @Test
  void notNull_DE() {
    var bean = new AllSortsBean();
    bean.myNotNull = null;
    ConstraintViolation constraint = one(bean, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("darf nicht null sein");
  }

  @Test
  void notBlank_EN() {
    var bean = new AllSortsBean();
    bean.myNotBlank = null;
    ConstraintViolation constraint = one(bean, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must not be blank");
  }

  @Test
  void notBlank_DE() {
    var bean = new AllSortsBean();
    bean.myNotBlank = null;
    ConstraintViolation constraint = one(bean, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("darf nicht leer sein");
  }

  @Test
  void notEmpty_EN() {
    var bean = new AllSortsBean();
    bean.myNotEmpty = null;
    ConstraintViolation constraint = one(bean, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must not be empty");
  }

  @Test
  void notEmpty_DE() {
    var bean = new AllSortsBean();
    bean.myNotEmpty = null;
    ConstraintViolation constraint = one(bean, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("darf nicht leer sein");
  }

}
