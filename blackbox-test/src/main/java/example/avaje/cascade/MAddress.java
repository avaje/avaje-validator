package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Size;
import io.avaje.validation.constraints.Valid;

@Valid
public class MAddress {

  @NotBlank @Size(max = 10)
  public String line1;
  public String line2;

}
