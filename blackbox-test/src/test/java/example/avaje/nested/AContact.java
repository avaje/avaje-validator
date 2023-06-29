package example.avaje.nested;

import io.avaje.validation.ValidPojo;
import io.avaje.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@ValidPojo
public class AContact {

  @NotBlank
  public String firstName;
  @Size(max = 5)
  public String lastName;

  @Valid
  @NotNull
  public AAddress address;

  public AContact(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

}
