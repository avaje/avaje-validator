package example.avaje.method2;

import io.avaje.http.api.Validator;
import io.avaje.validation.ValidMethod;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;

@Singleton
public class MethodTest {

  public MethodTest(Validator apiValidator) {}

  @NotEmpty
  @ValidMethod(throwOnParamFailure = false)
  String test(@NotEmpty String regular) {
    return regular;
  }
}
