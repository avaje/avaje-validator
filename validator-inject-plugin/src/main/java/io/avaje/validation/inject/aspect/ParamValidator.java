package io.avaje.validation.inject.aspect;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;

// Imported into scope
public class ParamValidator implements AspectProvider<ValidateParams> {

  final ValidationContext ctx;
  private final Map<Method, List<ValidationAdapter<Object>>> paramAdapters;

  public ParamValidator(Validator validator, List<ParamAdapterProvider> adapterProviders) {
    this.ctx = (ValidationContext) validator;
    this.paramAdapters =
        adapterProviders.stream()
            .collect(toMap(ParamAdapterProvider::provide, p -> p.paramAdapters(this.ctx)));
  }

  @Override
  public MethodInterceptor interceptor(Method method, ValidateParams aspectAnnotation) {

    return new ParamInterceptor(paramAdapters.get(method), ctx);
  }
}
