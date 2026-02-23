package io.avaje.validation.generator.models.valid;

import io.avaje.http.api.Valid;
import io.avaje.validation.constraints.Pattern;

@Valid
public class Address {
  @Pattern(
      regexp = "^(?=^.{1,16}$)^\\d+\\.\\d{2}$",
      message = "Not a valid amount. please add 2 decimal behind")
  public String line1;
  public String line2;
  @PrimitiveTest public long longValue;
}
