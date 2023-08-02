package example.avaje.controller;

import io.avaje.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MyControllerTest {

  final Validator validator = Validator.builder().build();

  @Test
  void expectNoValidator() {
    String message = assertThrows(IllegalArgumentException.class, () -> {
      validator.validate(new MyController());
    }).getMessage();

    assertThat(message).contains("No ValidationAdapter for class example.avaje.controller.MyController");
  }
}
