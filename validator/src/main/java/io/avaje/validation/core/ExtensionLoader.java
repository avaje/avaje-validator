package io.avaje.validation.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import io.avaje.validation.spi.AdapterFactory;
import io.avaje.validation.spi.AnnotationFactory;
import io.avaje.validation.spi.GeneratedComponent;
import io.avaje.validation.spi.MessageInterpolator;
import io.avaje.validation.spi.ValidationExtension;
import io.avaje.validation.spi.ValidatorCustomizer;

/** Load all the services using the common service interface. */
final class ExtensionLoader {

  private final List<GeneratedComponent> generatedComponents = new ArrayList<>();
  private final List<ValidatorCustomizer> customizers = new ArrayList<>();
  private final List<AdapterFactory> adapterFactories = new ArrayList<>();
  private final List<AnnotationFactory> annotationFactories = new ArrayList<>();
  private Optional<MessageInterpolator> interpolator = Optional.empty();

  ExtensionLoader(ClassLoader classLoader) {
    for (var spi : ServiceLoader.load(ValidationExtension.class, classLoader)) {
      if (spi instanceof GeneratedComponent gc) {
        generatedComponents.add(gc);
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

  Optional<MessageInterpolator> interpolator() {
    return interpolator;
  }

  List<GeneratedComponent> generatedComponents() {
    return generatedComponents;
  }

  List<ValidatorCustomizer> customizers() {
    return customizers;
  }

  List<AdapterFactory> adapterFactories() {
    return adapterFactories;
  }

  List<AnnotationFactory> annotationFactories() {
    return annotationFactories;
  }
}
