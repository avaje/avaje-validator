package io.avaje.validation.adapter;

import java.lang.reflect.Method;
import java.util.List;

public interface MethodAdapterProvider {

  Method method() throws Exception;

  List<ValidationAdapter<Object>> paramAdapters(ValidationContext ctx);

  default ValidationAdapter<Object> returnAdapter(ValidationContext ctx) {
    return ctx.noop();
  }

  default ValidationAdapter<Object[]> crossParamAdapter(ValidationContext ctx) {
    return ctx.noop();
  }

  default Method provide() {
    try {
      return method();
    } catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
