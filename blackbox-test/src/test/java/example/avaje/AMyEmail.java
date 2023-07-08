package example.avaje;

import io.avaje.validation.constraints.Email;
import io.avaje.validation.constraints.Valid;

@Valid
public class AMyEmail {

  @Email
  public final String email;

  public AMyEmail(String email) {
    this.email = email;
  }
}
