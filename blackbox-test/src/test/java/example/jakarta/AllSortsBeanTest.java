package example.jakarta;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class AllSortsBeanTest {

  final Validator validator = Validator.builder().addLocals(Locale.GERMAN).build();

  protected ConstraintViolation one(AllSortsBean pojo, Locale locale) {
    try {
      validator.validate(pojo, locale);
      throw new IllegalStateException("don't get here");
    } catch (ConstraintViolationException e) {
      var violations = new ArrayList<>(e.violations());
      assertThat(violations).hasSize(1);
      return violations.get(0);
    }
  }

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

  @Test
  void assertTrue_EN() {
    var bean = new AllSortsBean();
    bean.myAssertTrue = false;
    ConstraintViolation constraint = one(bean, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must be true");
  }

  @Test
  void assertTrue_DE() {
    var bean = new AllSortsBean();
    bean.myAssertTrue = false;
    ConstraintViolation constraint = one(bean, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("muss wahr sein");
  }

  @Test
  void assertFalse_EN() {
    var bean = new AllSortsBean();
    bean.myAssertFalse = true;
    ConstraintViolation constraint = one(bean, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must be false");
  }

  @Test
  void assertFalse_DE() {
    var bean = new AllSortsBean();
    bean.myAssertFalse = true;
    ConstraintViolation constraint = one(bean, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("muss falsch sein");
  }

  @Test
  void null_EN() {
    var bean = new AllSortsBean();
    bean.myNull = "Invalid";
    ConstraintViolation constraint = one(bean, Locale.ENGLISH);
    assertThat(constraint.message()).isEqualTo("must be null");
  }

  @Test
  void null_DE() {
    var bean = new AllSortsBean();
    bean.myNull = "Invalid";
    ConstraintViolation constraint = one(bean, Locale.GERMAN);
    assertThat(constraint.message()).isEqualTo("muss null sein");
  }

}
