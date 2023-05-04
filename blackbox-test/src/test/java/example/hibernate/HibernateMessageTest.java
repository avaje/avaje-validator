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

    private ConstraintViolation<HCustomer> one(HCustomer cust) {
        List<ConstraintViolation<HCustomer>> violations = new ArrayList<>(validator.validate(cust));
        assertThat(violations).hasSize(1);
        return violations.get(0);
    }

}
