package io.avaje.validation.inject.aspect;

import java.util.List;

import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Positive;
import jakarta.inject.Singleton;

@Singleton
public class MethodTest {

  @ValidateParams
  public void test(@NotEmpty List<@NotNull String> str, @Positive int inty, String regular) {}
}
