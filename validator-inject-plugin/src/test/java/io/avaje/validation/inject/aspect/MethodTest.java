package io.avaje.validation.inject.aspect;

import java.util.List;

import io.avaje.validation.ValidMethod;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Positive;
import jakarta.inject.Singleton;

@Singleton
public class MethodTest {

  @ValidMethod
  public void test(@NotEmpty List<@NotNull String> str, @Positive int inty, String regular) {}
}
