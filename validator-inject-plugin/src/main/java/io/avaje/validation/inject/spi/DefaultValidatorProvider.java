package io.avaje.validation.inject.spi;

import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;

import io.avaje.inject.BeanScopeBuilder;
import io.avaje.inject.aop.Aspect;
import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.spi.GenericType;
import io.avaje.inject.spi.InjectPlugin;
import io.avaje.validation.ValidMethod;
import io.avaje.validation.Validator;
import io.avaje.validation.adapter.MethodAdapterProvider;
import io.avaje.validation.inject.aspect.AOPMethodValidator;

/** Plugin for avaje inject that provides a default Validator instance. */
public final class DefaultValidatorProvider implements InjectPlugin {

  private static final boolean WIRE_ASPECTS = aspectsOnClasspath();

  private static boolean aspectsOnClasspath() {
    var modules = ModuleLayer.boot();
    return modules
      .findModule("io.avaje.validation.plugin")
      .map(m -> modules.findModule("io.avaje.inject.aop").isPresent())
      .orElseGet(() -> {
        try {
          return Aspect.class != null;
        } catch (Throwable e) {
          return false;
        }
      });
  }

  @Override
  public Class<?>[] provides() {
    return new Class<?>[] {Validator.class};
  }

  @Override
  public Class<?>[] providesAspects() {
    return WIRE_ASPECTS ? new Class<?>[] {ValidMethod.class} : new Class<?>[] {};
  }

  @Override
  public void apply(BeanScopeBuilder builder) {
    validator(builder);
    if (WIRE_ASPECTS) {
      paramAspect(builder);
    }
  }

  private void validator(BeanScopeBuilder builder) {
    builder.provideDefault(
        null,
        Validator.class,
        () -> {
          final var props = builder.configPlugin();
          final var validator =
              Validator.builder().failFast(props.equalTo("validation.failFast", "true"));

          props
              .get("validation.resourcebundle.names")
              .map(s -> s.split(","))
              .ifPresent(validator::addResourceBundles);

          props
              .get("validation.locale.default")
              .map(Locale::forLanguageTag)
              .ifPresent(validator::setDefaultLocale);

          props.get("validation.locale.addedLocales").stream()
              .flatMap(s -> Arrays.stream(s.split(",")))
              .map(Locale::forLanguageTag)
              .forEach(validator::addLocales);

          props
              .get("validation.temporal.tolerance.value")
              .map(Long::valueOf)
              .ifPresent(
                  duration -> {
                    final var unit =
                        props
                            .get("validation.temporal.tolerance.chronoUnit")
                            .map(ChronoUnit::valueOf)
                            .orElse(ChronoUnit.MILLIS);
                    validator.temporalTolerance(Duration.of(duration, unit));
                  });
          return validator.build();
        });
  }

  private void paramAspect(BeanScopeBuilder builder) {
    builder.provideDefault(
        null,
        new GenericType<AspectProvider<ValidMethod>>() {}.type(),
        () -> {
          final var methodValidator = new AOPMethodValidator();

          builder.addPostConstruct(
              b -> {
                final var ctx = b.get(Validator.class).context();
                final var map =
                    b.list(MethodAdapterProvider.class).stream()
                        .collect(toMap(MethodAdapterProvider::provide, p -> p));
                methodValidator.post(ctx, map);
              });

          return methodValidator;
        });
  }
}
