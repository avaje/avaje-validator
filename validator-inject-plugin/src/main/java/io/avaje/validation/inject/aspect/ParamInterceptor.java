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

    final var args = invocation.arguments();
    final var req = ctx.request(locale, List.of());
    var i = 0;
    for (final var adapter : validationAdapters) {
      final Object object = args[i];
      adapter.validate(object, req);
      ++i;
    }

    req.throwWithViolations();
    invocation.arguments();
  }
}
