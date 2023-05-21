package example.avaje;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Valid
public class ACustomer {

    @NotBlank @Size(max = 5)
    final String name;

    @NotBlank @Size(max = 7, message = "My custom error message with max {max}")
    final String other;

    @Size(min = 2, max = 4)
    final String minMax;

    public ACustomer(String name, String other, String minMax) {
      this.name = name;
      this.other = other;
      this.minMax = minMax;
    }

    public ACustomer(String name, String other) {
      this(name, other, "val");
    }

    public ACustomer(String name) {
      this(name, "valid");
    }

    public String getName() {
        return name;
    }

    public String getOther() {
        return other;
    }

    public String minMax() {
      return minMax;
    }
}
