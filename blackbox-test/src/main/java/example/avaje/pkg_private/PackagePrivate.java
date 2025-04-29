package example.avaje.pkg_private;

import io.avaje.validation.constraints.Positive;
import jakarta.validation.Valid;

@Valid
record PackagePrivate(@Positive int id) {}
