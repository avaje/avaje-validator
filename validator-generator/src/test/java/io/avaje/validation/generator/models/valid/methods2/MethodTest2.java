package io.avaje.validation.generator.models.valid.methods2;

import io.avaje.inject.Component;
import io.avaje.validation.ValidMethod;
import io.avaje.validation.constraints.NotEmpty;

@Component
public class MethodTest2 {
  @NotEmpty
  @ValidMethod
  String test(@NotEmpty String regular) {
    return regular;
  }
}
