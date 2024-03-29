package io.avaje.validation.generator.models.valid.methods;

import java.util.List;

import io.avaje.inject.Component;
import io.avaje.validation.ValidMethod;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Positive;
import io.avaje.validation.constraints.Valid;
import io.avaje.validation.generator.models.valid.CrewMate;

@Component
public class MethodTest {
  @NotEmpty
  @ValidMethod
  String test(@NotEmpty List<@Valid CrewMate> crew, @Positive int inty, String regular) {
    return regular;
  }
}
