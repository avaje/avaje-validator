package io.avaje.validation.inject.aspect;

import java.util.List;
import java.util.Locale;

import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

public class ParamInterceptor implements MethodInterceptor {
  private final List<ValidationAdapter<Object>> validationAdapters;
  private final ValidationContext ctx;
  private final Locale locale;

  public ParamInterceptor(
      Locale locale, ValidationContext ctx, List<ValidationAdapter<Object>> adapters) {
    this.locale = locale;
    this.ctx = ctx;
    this.validationAdapters = adapters;
  }

  @Override
  public void invoke(Invocation invocation) throws Throwable {
    var i = 0;
    final var args = invocation.arguments();
    for (final var adapter : validationAdapters) {
      final Object object = args[i];
      final var req = ctx.request(locale, List.of());
      adapter.validate(object, req);
      req.throwWithViolations();

      i++;
    }

    invocation.arguments();
  }
}
