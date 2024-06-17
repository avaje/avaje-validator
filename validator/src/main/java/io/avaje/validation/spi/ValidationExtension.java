package io.avaje.validation.spi;

import io.avaje.spi.Service;

/** super interface for all Validation SPIs */
@Service
public sealed interface ValidationExtension
    permits AdapterFactory,
        AnnotationFactory,
        GeneratedComponent,
        MessageInterpolator,
        ValidatorCustomizer {}
