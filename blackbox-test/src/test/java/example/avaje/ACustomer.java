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

    public ACustomer(String name, String other) {
        this.name = name;
        this.other = other;
    }
    public ACustomer(String name) {
        this.name = name;
        this.other = "valid";
    }

    public String getName() {
        return name;
    }

    public String getOther() {
        return other;
    }
}
