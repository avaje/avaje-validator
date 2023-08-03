package example.avaje.length;

import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class BStringMaxTest {

  final Validator validator = Validator.builder().addLocales(Locale.GERMAN).build();

  @Test
  void check() {
    var violations = validator.check(new BStringMax("ok", 4));
    assertThat(violations).isEmpty();

    var violations2 = validator.check(new BStringMax2("ok", 4));
    assertThat(violations2).isEmpty();

  }

  @Test
  void invalidMaxOnString() {
    var violations = new ArrayList<>(validator.check(new BStringMax("12345", 4)));
    assertThat(violations).hasSize(1);
    assertThat(violations.get(0).message()).isEqualTo("maximum length 4 exceeded");
  }

  @Test
  void invalidBoth_showsDifferentMessages() {
    var violations = new ArrayList<>(validator.check(new BStringMax("12345", 5)));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("maximum length 4 exceeded");
    assertThat(violations.get(1).message()).isEqualTo("must be less than or equal to 4");
  }

  @Test
  void invalidBoth_showsDifferentMessages_DE() {
    var violations = new ArrayList<>(validator.check(new BStringMax("12345", 6), Locale.GERMAN));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("Länge muss zwischen 0 und 4 sein");
    assertThat(violations.get(1).message()).isEqualTo("muss kleiner-gleich 4 sein");
  }

  @Test
  void invalidBoth_customMessages() {
    var violations = new ArrayList<>(validator.check(new BStringMax2("12345", 6)));
    assertThat(violations).hasSize(2);
    assertThat(violations.get(0).message()).isEqualTo("customMaxStr4");
    assertThat(violations.get(1).message()).isEqualTo("customMaxInt4");
  }
}
