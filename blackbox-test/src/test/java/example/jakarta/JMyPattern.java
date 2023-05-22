package example.jakarta;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

@Valid
public class JMyPattern {

  @Pattern(regexp = "[0-3]+")
  public final String myPattern;

  public JMyPattern(String myPattern) {
    this.myPattern = myPattern;
  }
}
