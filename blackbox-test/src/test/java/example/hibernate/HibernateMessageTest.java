package example.hibernate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolverContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HibernateMessageTest {

    private static final Set<Locale> locales = Set.of(Locale.ENGLISH, Locale.GERMAN);
    private final Validator validator = Validation
            .byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator(locales, Locale.ENGLISH, new TestLocaleResolver(), true))
            .buildValidatorFactory()
            .getValidator();

    private Locale testLocale = Locale.ENGLISH;

    class TestLocaleResolver implements LocaleResolver {
        @Override
        public Locale resolve(LocaleResolverContext localeResolverContext) {
            return testLocale;
        }
    }

    @Test
    void notBlank() {
        var cust = new HCustomer(null);
        ConstraintViolation<HCustomer> err = one(cust);
        assertThat(err.getMessage()).isEqualTo("must not be blank");
        assertThat(err.getMessageTemplate()).isEqualTo("{jakarta.validation.constraints.NotBlank.message}");
    }

    @Test
    void sizeMax() {
        var cust = new HCustomer("fooBar");
        ConstraintViolation<HCustomer> err = one(cust);
        assertThat(err.getMessage()).isEqualTo("size must be between 0 and 5");
    }

    @Test
    void sizeMaxLocaleGerman() {
        var cust = new HCustomer("fooBar");
        testLocale = Locale.GERMAN;
        ConstraintViolation<HCustomer> err = one(cust);
        testLocale = Locale.getDefault();
        assertThat(err.getMessage()).isEqualTo("Größe muss zwischen 0 und 5 sein");
    }

    @Test
    void sizeMaxCustomMessage() {
        var cust = new HCustomer("ok", "IAmNotValidHere");
        ConstraintViolation<HCustomer> err = one(cust);
        assertThat(err.getMessage()).isEqualTo("My custom error message with max 7");
    }

  @Test
  void nested() {
    var customer = new HCustomer("ok", "IAmNotValidHere");
    customer.contacts().add(new HContact("", 42));
    customer.contacts().add(new HContact("ok", 23));
    customer.contacts().add(new HContact(null, -23));

    Set<ConstraintViolation<HCustomer>> violations = validator.validate(customer);
    assertThat(violations).hasSize(4);

    ConstraintViolation<HCustomer> v0 = forPath(violations, "other");
    assertThat(v0.getMessage()).isEqualTo("My custom error message with max 7");

    ConstraintViolation<HCustomer> v1 = forPath(violations, "contacts[0].name");
    assertThat(v1.getMessage()).isEqualTo("must not be empty");

    ConstraintViolation<HCustomer> v2 = forPath(violations, "contacts[2].score");
    assertThat(v2.getMessage()).isEqualTo("must be greater than 0");

    ConstraintViolation<HCustomer> v3 = forPath(violations, "contacts[2].name");
    assertThat(v3.getMessage()).isEqualTo("must not be empty");
  }

  <T> ConstraintViolation<T> forPath(Set<ConstraintViolation<T>> violations, String path) {
    return violations.stream()
      .filter(violation -> path.equals(violation.getPropertyPath().toString()))
      .findFirst().orElseThrow();
  }

    private ConstraintViolation<HCustomer> one(HCustomer cust) {
        List<ConstraintViolation<HCustomer>> violations = new ArrayList<>(validator.validate(cust));
        assertThat(violations).hasSize(1);
        return violations.get(0);
    }

}
