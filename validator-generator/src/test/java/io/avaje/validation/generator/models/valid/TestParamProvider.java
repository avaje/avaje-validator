package io.avaje.validation.generator.models.valid;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.avaje.inject.Component;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Positive;
import io.avaje.validation.generator.models.valid.methods.MethodTest;
import io.avaje.validation.inject.aspect.ParamAdapterProvider;
import io.avaje.validation.inject.aspect.ParamValidator;
import io.avaje.validation.spi.Generated;

@Generated
@Component
@Component.Import(ParamValidator.class)
public final class TestParamProvider implements ParamAdapterProvider {

  @Override
  public Method method() throws Exception {

    return MethodTest.class.getDeclaredMethod("test", List.class, int.class);
  }

  @Override
  public List<ValidationAdapter<Object>> paramAdapters(ValidationContext ctx) {
    return List.of(
        ctx.<Object>adapter(
                NotEmpty.class, Map.of("groups", Set.of(), "message", "{avaje.NotEmpty.message}"))
            .list()
            .andThenMulti(ctx.adapter(CrewMate.class)),
        ctx.<Object>adapter(
            Positive.class, Map.of("groups", Set.of(), "message", "{avaje.Positive.message}")));
  }
}
