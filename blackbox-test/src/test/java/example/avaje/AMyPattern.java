package example.avaje;

import io.avaje.validation.Valid;
import io.avaje.validation.constraints.Pattern;

@Valid
public class AMyPattern {

  @Pattern(regexp = "[0-3]+")
  public final String myPattern;

  public AMyPattern(String myPattern) {
    this.myPattern = myPattern;
  }
}
