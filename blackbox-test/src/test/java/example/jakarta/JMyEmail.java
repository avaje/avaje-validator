package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@Valid
public class JMyEmail {

  @Email
  public final String email;

  public JMyEmail(String email) {
    this.email = email;
  }
}
