package io.avaje.validation.spring.aspect;

import java.util.List;
import java.util.Locale;

import org.aspectj.lang.ProceedingJoinPoint;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

final class ParamInterceptor {

  private final Locale locale;
  private final boolean throwOnParamFailure;
  private final List<ValidationAdapter<?>> paramValidationAdapter;
  private final ValidationAdapter<Object> returnValidationAdapter;
  private final ValidationContext ctx;
  private final ValidationAdapter<Object[]> crossParamAdapter;

  public ParamInterceptor(
      Locale locale,
      boolean throwOnParamFailure,
      ValidationContext ctx,
      List<ValidationAdapter<?>> paramValidationAdapter,
      ValidationAdapter<Object> returnValidationAdapter,
      ValidationAdapter<Object[]> crossParamAdapter) {
    this.locale = locale;
    this.throwOnParamFailure = throwOnParamFailure;
    this.paramValidationAdapter = paramValidationAdapter;
    this.returnValidationAdapter = returnValidationAdapter;
    this.ctx = ctx;
    this.crossParamAdapter = crossParamAdapter;
  }

  public void invoke(ProceedingJoinPoint invocation) throws Throwable {
    final var args = invocation.getArgs();
    final var req = ctx.request(locale, List.of());
    var i = 0;
    for (final var adapter : paramValidationAdapter) {
      final Object object = args[i];

      ((ValidationAdapter<Object>) adapter).validate(object, req);
      ++i;
    }
    crossParamAdapter.validate(args, req);
    if (throwOnParamFailure) {
      req.throwWithViolations();
    }
    returnValidationAdapter.validate(invocation.proceed(), req);
    req.throwWithViolations();
  }
}
