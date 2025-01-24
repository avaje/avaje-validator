package io.avaje.validation.generator.models.valid;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Valid
public class SignupRequest {

  @NotBlank(message = "{signup.password.notblank1}")
  @Size(min = 16, max = 32, message = "{signup.password.size}")
  @Pattern(regexp = "^[a-zA-Z0-9!@#$^&*]*$", message = "{signup.password.invalid}")
  @Pattern(regexp = ".*[a-z].*", message = "{signup.password.lowercase}")
  @Pattern(regexp = ".*[A-Z].*", message = "{signup.password.uppercase}")
  @Pattern(regexp = ".*[0-9].*", message = "{signup.password.digit}")
  @Pattern(regexp = ".*[!@#$^&*].*", message = "{signup.password.special}")
  private String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
