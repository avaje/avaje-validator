package example.avaje.typeuse;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.avaje.http.api.ValidationException;
import io.avaje.http.api.Validator;
import io.avaje.inject.spi.AvajeModule;
import io.avaje.inject.spi.Builder;
import io.avaje.inject.test.InjectTest;
import jakarta.inject.Inject;

@InjectTest
class DefaultValidatorProviderTest {
  AvajeModule mod =
      new AvajeModule() {

        @Override
        public Class<?>[] classes() {
          return new Class<?>[] {};
        }

        @Override
        public void build(Builder builder) {}
      };

  @Inject private Validator validator;

  @BeforeAll
  static void setLocale() {
    System.setProperty("validation.locale.default", "en-us");
  }

  @Test
  void test() {
    assertAll(() -> validator.validate(new CrewMate("hmm"), "en-GB,en;q=0.9,en-US;q=0.8,de;q=0.7"));
  }

  @Test
  void test2() {
    assertThrows(
        ValidationException.class,
        () -> validator.validate(new CrewMate(""), "en-GB,en;q=0.9,en-US;q=0.8,de;q=0.7"));
  }
}
