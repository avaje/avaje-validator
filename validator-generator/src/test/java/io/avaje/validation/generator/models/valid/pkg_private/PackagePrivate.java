package io.avaje.validation.generator.models.valid.pkg_private;

import io.avaje.validation.constraints.Positive;
import jakarta.validation.Valid;

@Valid
record PackagePrivate(@Positive Long id) {}
