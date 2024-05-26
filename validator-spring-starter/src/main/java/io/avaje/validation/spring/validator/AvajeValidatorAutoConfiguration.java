package io.avaje.validation.spring.validator;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.avaje.validation.Validator;

/** Autoconfiguration of Avaje Validator. */
@Configuration
public class AvajeValidatorAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  Validator validator(
      @Value("${validation.failFast:false}") boolean failFast,
      @Value("${validation.resourcebundle.names:#{null}}") Optional<String> resourceBundleNames,
      @Value("${validation.locale.default:#{null}}") Optional<String> defaultLocal,
      @Value("${validation.locale.addedLocales:#{null}}") Optional<String> addedLocales,
      @Value("${validation.temporal.tolerance.value:#{null}}") Optional<String> temporalTolerance,
      @Value("${validation.temporal.tolerance.chronoUnit:MILLIS}") ChronoUnit chronoUnit) {
    final var validator = Validator.builder().failFast(failFast);

    resourceBundleNames.map(s -> s.split(",")).ifPresent(validator::addResourceBundles);
    defaultLocal.map(Locale::forLanguageTag).ifPresent(validator::setDefaultLocale);

    addedLocales.stream()
        .flatMap(s -> Arrays.stream(s.split(",")))
        .map(Locale::forLanguageTag)
        .forEach(validator::addLocales);

    temporalTolerance
        .map(Long::valueOf)
        .ifPresent(
            duration -> {
              final var unit = chronoUnit;
              validator.temporalTolerance(Duration.of(duration, unit));
            });
    return validator.build();
  }
}
