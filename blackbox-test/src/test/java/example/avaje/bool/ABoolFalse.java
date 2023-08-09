package example.avaje.bool;

import io.avaje.validation.constraints.AssertFalse;
import io.avaje.validation.constraints.Valid;

@Valid
public record ABoolFalse(

  @AssertFalse boolean primitive,
  @AssertFalse Boolean object
) {

  @AssertFalse public boolean primitiveMethod() {
    return primitive;
  }

  @AssertFalse public Boolean objectMethod() {
    return object;
  }
}
