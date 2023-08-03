package example.avaje;

import example.avaje.cascade.ACrew;
import example.avaje.cascade.BShip;
import example.avaje.cascade.DShip;
import io.avaje.validation.ConstraintViolationException;
import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ExceptionMessageTest {

  Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void oneError() {
    var ship = new DShip("", "ok", new String[]{"ok"});
    try {
      validator.validate(ship);
      fail("don't get here");
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(1);
      assertThat(e).hasMessage("1 constraint violation(s) occurred.\n" +
        " name: must not be blank");
    }
  }

  @Test
  void twoErrors() {
    var ship = new DShip("", "", new String[]{"ok"});
    try {
      validator.validate(ship);
      fail("don't get here");
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(2);
      assertThat(e).hasMessage("2 constraint violation(s) occurred.\n" +
        " name: must not be blank\n" +
        " rating: must not be blank");
    }
  }

  @Test
  void threeErrors() {
    var ship = new DShip("", "", null);
    try {
      validator.validate(ship);
      fail("don't get here");
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(3);
      assertThat(e).hasMessage("3 constraint violation(s) occurred.\n" +
        " name: must not be blank\n" +
        " rating: must not be blank\n" +
        " crew: must not be empty");
    }
  }

  @Test
  void tenErrors() {
    var ship = new BShip("", createBadCrew(9));
    try {
      validator.validate(ship);
      fail("don't get here");
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(10);
      assertThat(e).hasMessage("10 constraint violation(s) occurred.\n" +
        " name: must not be blank\n" +
        " crew[0].name: maximum length 4 exceeded\n" +
        " crew[1].name: maximum length 4 exceeded\n" +
        " crew[2].name: maximum length 4 exceeded\n" +
        " crew[3].name: maximum length 4 exceeded\n" +
        " crew[4].name: maximum length 4 exceeded\n" +
        " crew[5].name: maximum length 4 exceeded\n" +
        " crew[6].name: maximum length 4 exceeded\n" +
        " crew[7].name: maximum length 4 exceeded\n" +
        " crew[8].name: maximum length 4 exceeded");
    }
  }

  @Test
  void elevenErrors() {
    var ship = new BShip("", createBadCrew(10));
    try {
      validator.validate(ship);
      fail("don't get here");
    } catch (ConstraintViolationException e) {
      assertThat(e.violations()).hasSize(11);
      assertThat(e).hasMessage("11 constraint violation(s) occurred.\n" +
        " name: must not be blank\n" +
        " crew[0].name: maximum length 4 exceeded\n" +
        " crew[1].name: maximum length 4 exceeded\n" +
        " crew[2].name: maximum length 4 exceeded\n" +
        " crew[3].name: maximum length 4 exceeded\n" +
        " crew[4].name: maximum length 4 exceeded\n" +
        " crew[5].name: maximum length 4 exceeded\n" +
        " crew[6].name: maximum length 4 exceeded\n" +
        " crew[7].name: maximum length 4 exceeded\n" +
        " crew[8].name: maximum length 4 exceeded\n" +
        " and 1 other error(s)");
    }
  }

  private Set<ACrew> createBadCrew(int count) {
    Set<ACrew> set = new LinkedHashSet<>();
    for (int i = 0; i < count; i++) {
      set.add(new ACrew("abcd" + i));
    }
    return set;
  }

}
