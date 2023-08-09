package example.avaje.bool;

import io.avaje.validation.constraints.AssertTrue;
import io.avaje.validation.constraints.Valid;

@Valid
public record ABoolTrue(
  @AssertTrue boolean primitive,
  @AssertTrue Boolean object
) {
}
