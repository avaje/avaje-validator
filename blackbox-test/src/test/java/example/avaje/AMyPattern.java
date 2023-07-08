package example.avaje;

import io.avaje.validation.constraints.Pattern;
import io.avaje.validation.constraints.Valid;

@Valid
public class AMyPattern {

  @Pattern(regexp = "[0-3]+")
  public final String myPattern;

  public AMyPattern(String myPattern) {
    this.myPattern = myPattern;
  }
}
