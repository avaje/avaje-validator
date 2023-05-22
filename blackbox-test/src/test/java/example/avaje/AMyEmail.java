package example.avaje;

import io.avaje.validation.ValidPojo;
import io.avaje.validation.constraints.Email;

@ValidPojo
public class AMyEmail {

  @Email
  public final String email;

  public AMyEmail(String email) {
    this.email = email;
  }
}
