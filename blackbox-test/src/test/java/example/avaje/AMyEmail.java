package example.avaje;

import io.avaje.validation.Valid;
import io.avaje.validation.constraints.Email;

@Valid
public class AMyEmail {

  @Email
  public final String email;

  public AMyEmail(String email) {
    this.email = email;
  }
}
