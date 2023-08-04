package example.avaje.notblank;

import io.avaje.validation.constraints.Valid;

@Valid
public record AStringOnlyCheck(

  // @NotBlank
  int notString1,

  // @Email
  Boolean notString2

) {
}
