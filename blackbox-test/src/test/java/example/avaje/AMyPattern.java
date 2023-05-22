package example.avaje;

import io.avaje.validation.ValidPojo;
import io.avaje.validation.constraints.Pattern;

@ValidPojo
public class AMyPattern {

  @Pattern(regexp = "[0-3]+")
  public final String myPattern;

  public AMyPattern(String myPattern) {
    this.myPattern = myPattern;
  }
}
