package io.avaje.validation.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import io.avaje.validation.spi.AdapterFactory;
import io.avaje.validation.spi.AnnotationFactory;
import io.avaje.validation.spi.GeneratedComponent;
import io.avaje.validation.spi.MessageInterpolator;
import io.avaje.validation.spi.ValidatorCustomizer;
import io.avaje.validation.spi.ValidatorExtension;

/** Load all the services using the common service interface. */
final class DServiceLoader {

  private final List<GeneratedComponent> components = new ArrayList<>();
  private final List<ValidatorCustomizer> customizers = new ArrayList<>();
  private final List<AdapterFactory> adapterFactories = new ArrayList<>();
  private final List<AnnotationFactory> annotationFactories = new ArrayList<>();
  private Optional<MessageInterpolator> interpolator = Optional.empty();

  DServiceLoader() {
    for (var spi : ServiceLoader.load(ValidatorExtension.class)) {
      if (spi instanceof GeneratedComponent gc) {
        components.add(gc);
      } else if (spi instanceof ValidatorCustomizer c) {
        customizers.add(c);
      } else if (spi instanceof MessageInterpolator m) {
        interpolator = Optional.of(m);
      } else if (spi instanceof AdapterFactory af) {
        adapterFactories.add(af);
      } else if (spi instanceof AnnotationFactory af) {
        annotationFactories.add(af);
      }
    }
  }

  public Optional<MessageInterpolator> interpolator() {
    return interpolator;
  }

  public List<GeneratedComponent> components() {
    return components;
  }

  public List<ValidatorCustomizer> customizers() {
    return customizers;
  }

  public List<AdapterFactory> adapterFactories() {
    return adapterFactories;
  }

  public List<AnnotationFactory> annotationFactories() {
    return annotationFactories;
  }
}
