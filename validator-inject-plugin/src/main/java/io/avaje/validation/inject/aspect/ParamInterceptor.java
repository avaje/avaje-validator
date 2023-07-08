package io.avaje.validation.inject.aspect;

import java.util.List;
import java.util.Locale;

import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

public class ParamInterceptor implements MethodInterceptor {

  private final List<ValidationAdapter<Object>> paramValidationAdapter;
  private final ValidationAdapter<Object> returnValidationAdapter;
  private final ValidationContext ctx;
  private final Locale locale;
  private final boolean throwOnParamFailure;

  public ParamInterceptor(
      Locale locale,
      ValidationContext ctx,
      MethodAdapterProvider methodAdapterProvider,
      boolean throwOnParamFailure) {

    this.locale = locale;
    this.ctx = ctx;
    this.paramValidationAdapter = methodAdapterProvider.paramAdapters(ctx);
    this.returnValidationAdapter = methodAdapterProvider.returnAdapter(ctx);
    this.throwOnParamFailure = throwOnParamFailure;
  }

  @Override
  public void invoke(Invocation invocation) throws Throwable {

    final var args = invocation.arguments();
    final var req = ctx.request(locale, List.of());
    var i = 0;
    for (final var adapter : paramValidationAdapter) {
      final Object object = args[i];
      adapter.validate(object, req);
      ++i;
    }

    if (throwOnParamFailure) {
      req.throwWithViolations();
    }

    returnValidationAdapter.validate(invocation.invoke(), req);

    req.throwWithViolations();
  }
}
