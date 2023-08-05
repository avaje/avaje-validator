package io.avaje.validation.inject.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import io.avaje.inject.PostConstruct;
import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.validation.ValidMethod;
import io.avaje.validation.adapter.MethodAdapterProvider;
import io.avaje.validation.adapter.ValidationContext;

public final class AOPMethodValidator implements AspectProvider<ValidMethod> {

  private final List<BiConsumer<ValidationContext, Map<Method, MethodAdapterProvider>>> consumers =
      new ArrayList<>();

  @PostConstruct
  public void post(ValidationContext ctx, Map<Method, MethodAdapterProvider> map) {

    consumers.forEach(c -> c.accept(ctx, map));
    consumers.clear();
  }

  @Override
  public MethodInterceptor interceptor(Method method, ValidMethod aspectAnnotation) {

    final var localeStr = aspectAnnotation.locale();
    final Locale locale;
    if (localeStr.isBlank()) {
      locale = null;
    } else {
      locale = Locale.forLanguageTag(localeStr);
    }
    final var interceptor =
        new ParamInterceptor(locale, method, aspectAnnotation.throwOnParamFailure());
    consumers.add(interceptor::postConstruct);
    return interceptor;
  }
}
