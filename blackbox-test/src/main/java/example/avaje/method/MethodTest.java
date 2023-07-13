package example.avaje.method;

import java.util.List;

import io.avaje.http.api.Validator;
import io.avaje.validation.inject.aspect.ValidateMethod;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Singleton
public class MethodTest {

  public MethodTest(Validator apiValidator) {}

  @NotNull
  @ValidateMethod
  String test(@NotEmpty List<@NotNull String> str, @Positive int inty, String regular) {
    return regular;
  }
}
