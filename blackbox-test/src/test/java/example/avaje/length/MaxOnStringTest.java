package example.avaje.length;

import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.constraints.Max;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MaxOnStringTest {

  @Test
  void expectCanNotCreate() {

    Validator build = Validator.builder().build();
    ValidationContext ctx = build.context();

    String message = assertThrows(IllegalStateException.class, () -> {
      ctx.<String>adapter(Max.class, Map.of("message", "{avaje.Max.message}", "value", 10L, "_type", "String"));
    }).getMessage();

    assertThat(message).isEqualTo("@Max is not allowed on a String type");
  }
}
