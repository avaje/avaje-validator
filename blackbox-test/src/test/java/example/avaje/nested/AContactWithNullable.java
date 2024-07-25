package example.avaje.nested;

import org.jspecify.annotations.Nullable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Valid
public class AContactWithNullable {

  @NotBlank
  public String firstName;
  @Size(max = 5)
  public String lastName;

  @Valid
  @Nullable
  public AAddress address;

  public AContactWithNullable(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

}
