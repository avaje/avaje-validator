package io.avaje.validation.inject.aspect;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationContext;

// Imported into scope
public class AOPMethodValidator implements AspectProvider<ValidateMethod> {

  final ValidationContext ctx;
  private final Map<Method, MethodAdapterProvider> paramAdapters;

  public AOPMethodValidator(Validator validator, List<MethodAdapterProvider> adapterProviders) {
    this.ctx = (ValidationContext) validator;
    this.paramAdapters =
        adapterProviders.stream().collect(toMap(MethodAdapterProvider::provide, p -> p));
  }

  @Override
  public MethodInterceptor interceptor(Method method, ValidateMethod aspectAnnotation) {

    final var localeStr = aspectAnnotation.locale();
    final Locale locale;
    if (localeStr.isBlank()) {
      locale = null;
    } else {
      locale = Locale.forLanguageTag(localeStr);
    }
    return new ParamInterceptor(locale, ctx, paramAdapters.get(method));
  }
}
