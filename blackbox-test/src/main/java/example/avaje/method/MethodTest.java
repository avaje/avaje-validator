package example.avaje.method;

import java.util.List;

import io.avaje.validation.inject.aspect.ValidateParams;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Singleton
public class MethodTest {

  @ValidateMethod
  void test(@NotEmpty List<@NotNull String> str, @Positive int inty, String regular) {}
}
