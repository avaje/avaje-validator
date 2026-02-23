package example.avaje.nested;

import io.avaje.validation.constraints.Valid;
import io.avaje.validation.constraints.Pattern;

@Valid
public class MyPatterns {

  @Pattern(
    regexp = "^(?=^.{1,16}$)^\\d+\\.\\d{2}$",
    message = "Not a valid amount. please add 2 decimal behind")
  private final String one;

  public MyPatterns(String one) {
    this.one = one;
  }

  public String one() {
    return one;
  }

}
