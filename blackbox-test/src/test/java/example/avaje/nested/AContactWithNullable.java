package example.avaje.nested;

import io.avaje.lang.Nullable;
import io.avaje.validation.ValidPojo;
import io.avaje.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@ValidPojo
public class AContactWithNullable {

  @NotBlank
  public String firstName;
  @Size(max = 5)
  public String lastName;

  @Nullable
  public AAddress address;

  public AContactWithNullable(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

}
