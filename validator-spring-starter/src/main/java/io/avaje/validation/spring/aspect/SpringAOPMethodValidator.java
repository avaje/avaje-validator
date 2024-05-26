package io.avaje.validation.spring.aspect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import io.avaje.validation.ValidMethod;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.MethodAdapterProvider;

@Aspect
public class SpringAOPMethodValidator {

  private final Map<Method, ParamInterceptor> interceptorMap = new HashMap<>();

  public SpringAOPMethodValidator(Validator validator, List<MethodAdapterProvider> providers) throws Exception {
    var ctx = validator.context();
    for (var provider : providers) {
      var method = provider.method();
      ValidMethod validMethod = method.getAnnotation(ValidMethod.class);
      final var localeStr = validMethod.locale();
      final Locale locale = localeStr.isBlank() ? null : Locale.forLanguageTag(localeStr);
      var paramValidationAdapter = provider.paramAdapters(ctx);
      var returnValidationAdapter = provider.returnAdapter(ctx);
      var crossParamAdapter = provider.crossParamAdapter(ctx);

      interceptorMap.put(
          method,
          new ParamInterceptor(
              locale,
              validMethod.throwOnParamFailure(),
              ctx,
              paramValidationAdapter,
              returnValidationAdapter,
              crossParamAdapter));
    }
  }

  @Around("@annotation(io.avaje.validation.ValidMethod)")
  public void interceptor(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    var methodValidator = interceptorMap.get(signature.getMethod());
    methodValidator.invoke(joinPoint);
  }
}
