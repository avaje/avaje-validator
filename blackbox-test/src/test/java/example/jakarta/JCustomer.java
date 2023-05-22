package example.jakarta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@Valid
public class JCustomer {

    @NotBlank @Size(max = 5)
    final String name;

    @NotBlank @Size(max = 7, message = "My custom error message with max {max}")
    final String other;

    @Size(min = 2, max = 4)
    final String minMax;

    public JCustomer(String name, String other, String minMax) {
      this.name = name;
      this.other = other;
      this.minMax = minMax;
    }

    public JCustomer(String name, String other) {
      this(name, other, "val");
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
