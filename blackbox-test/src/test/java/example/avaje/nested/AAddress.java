package example.avaje.nested;

import io.avaje.validation.constraints.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Valid
public class AAddress {

  @NotBlank
  public final String line1;
  @Size(max = 4)
  public final String line2;
  @Positive
  public final long longValue;

  public AAddress(String line1, String line2, long longValue) {
    this.line1 = line1;
    this.line2 = line2;
    this.longValue = longValue;
  }
}
