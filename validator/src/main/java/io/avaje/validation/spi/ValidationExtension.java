package io.avaje.validation.spi;

import io.avaje.spi.Service;

/** Super interface for all validation SPIs */
@Service
public sealed interface ValidationExtension
    permits AdapterFactory,
        AnnotationFactory,
        GeneratedComponent,
        MessageInterpolator,
        ValidatorCustomizer {}
