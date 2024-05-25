package io.avaje.validation.spring.aspect;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.avaje.validation.Validator;
import io.avaje.validation.adapter.MethodAdapterProvider;

@Configuration
@EnableAspectJAutoProxy
@ConditionalOnBean({Validator.class, MethodAdapterProvider.class})
public class MethodValidationAutoConfiguration {

  public SpringAOPMethodValidator validator(
      Validator validator, List<MethodAdapterProvider> providers) throws Exception {
    return new SpringAOPMethodValidator(validator, providers);
  }
}
