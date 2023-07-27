package example.hibernate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Valid
public class HCustomer {

    @NotBlank @Size(max = 5)
    final String name;

    @NotBlank @Size(max = 7, message = "My custom error message with max {max}")
    final String other;

    @Valid
    final List<HContact> contacts = new ArrayList<>();

    public HCustomer(String name, String other) {
        this.name = name;
        this.other = other;
    }
    public HCustomer(String name) {
        this.name = name;
        this.other = "valid";
    }

    public String getName() {
        return name;
    }

    public String getOther() {
        return other;
    }

  public List<HContact> contacts() {
    return contacts;
  }
}
