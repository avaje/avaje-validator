package io.avaje.validation.inject.aspect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

final class ParamInterceptor implements MethodInterceptor {

  private List<ValidationAdapter<Object>> paramValidationAdapter;
  private ValidationAdapter<Object> returnValidationAdapter;
  private ValidationContext ctx;
  private final Locale locale;
  private final boolean throwOnParamFailure;
  private final Method method;

  public ParamInterceptor(Locale locale, Method method, boolean throwOnParamFailure) {

    this.locale = locale;
    this.throwOnParamFailure = throwOnParamFailure;
    this.method = method;
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

  public void postConstruct(ValidationContext ctx, Map<Method, MethodAdapterProvider> providerMap) {

    final var methodAdapterProvider = providerMap.get(method);
    this.ctx = ctx;
    this.paramValidationAdapter = methodAdapterProvider.paramAdapters(ctx);
    this.returnValidationAdapter = methodAdapterProvider.returnAdapter(ctx);
  }
}
