package example.avaje.notblank;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ANumberCheckTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void valid() {
    var value = new ANumberCheck()
      .setCustomNumber(MyNumericType.of(3))
      .setMyScore(1)
      .setNotNumericTypeHere("ok")
      .setSomeCollection(List.of())
      .setNotNumericTypeHere("ok")
      .setActive(true)
      .setObjectActive(true)
      .setStringNotBoolean("blah");

    validator.validate(value);
  }

}
