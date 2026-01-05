package io.avaje.validation.spring.aspect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.avaje.validation.adapter.MethodAdapterProvider;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Positive;
import jakarta.inject.Singleton;

@Singleton
public final class TestParamProvider implements MethodAdapterProvider {

  @Override
  public Method method() throws Exception {

    return ValidMethodClass.class.getDeclaredMethod("test", List.class, int.class, String.class);
  }

  @Override
  public List<ValidationAdapter<?>> paramAdapters(ValidationContext ctx) {
    return List.of(
        ctx.<Object>adapter(
                NotEmpty.class, Map.of("groups", Set.of(), "message", "{avaje.NotEmpty.message}"))
            .list()
            .andThenMulti(
                ctx.adapter(
                    NotNull.class,
                    Map.of("groups", Set.of(), "message", "{avaje.NotEmpty.message}"))),
        ctx.<Object>adapter(
            Positive.class, Map.of("groups", Set.of(), "message", "{avaje.Positive.message}")),
        ctx.noop());
  }
}
