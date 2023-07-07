package io.avaje.validation.inject.aspect;

import java.lang.reflect.Method;
import java.util.List;

import io.avaje.inject.aop.InvocationException;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

public interface MethodAdapterProvider {

  Method method() throws Exception;

  List<ValidationAdapter<Object>> paramAdapters(ValidationContext ctx);

  default ValidationAdapter<Object> returnAdapter(ValidationContext ctx) {
    return ctx.noop();
  }

  default Method provide() {
    try {
      return method();
    } catch (final Exception e) {
      throw new InvocationException(e);
    }
  }
}
