package io.avaje.validation.generator.models.valid.methods;

import java.util.List;

import io.avaje.validation.Valid;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Positive;
import io.avaje.validation.generator.models.valid.CrewMate;
import io.avaje.validation.inject.aspect.ValidateParams;

public class MethodTest {

  @ValidateParams
  void test(@NotEmpty List<@Valid CrewMate> crew, @Positive int inty, String regular) {}
}
