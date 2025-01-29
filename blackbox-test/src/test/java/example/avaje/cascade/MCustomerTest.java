package example.avaje.cascade;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import io.avaje.validation.Validator;

import java.time.LocalDate;

class MCustomerTest {

  Validator validator = Validator.builder().build();

  @Test
  void valid() {
    var customer = new MCustomer()
      .setName("Foo")
      .setActiveDate(LocalDate.now())
      .setActive(true);

    validator.validate(customer);
  }
}
