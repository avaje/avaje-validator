package example.avaje.custom;

import io.avaje.validation.constraints.Valid;

@Valid
public record ACustomLong(
  @MyCustomALong long primitive,

  @MyCustomALong Long object
) {
}
