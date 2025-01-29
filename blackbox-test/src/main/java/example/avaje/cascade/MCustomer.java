package example.avaje.cascade;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Valid;

import java.time.LocalDate;

@Valid
public class MCustomer {

  boolean active;

  @NotBlank(max = 20)
  String name;

  @NotNull
  LocalDate activeDate;

  @Valid
  MAddress billingAddress;

  public MCustomer setActive(boolean active) {
    this.active = active;
    return this;
  }

  public MCustomer setName(String name) {
    this.name = name;
    return this;
  }

  public MCustomer setActiveDate(LocalDate activeDate) {
    this.activeDate = activeDate;
    return this;
  }

  public MCustomer setBillingAddress(MAddress billingAddress) {
    this.billingAddress = billingAddress;
    return this;
  }

  public boolean active() {
    return active;
  }

  public String name() {
    return name;
  }

  public LocalDate activeDate() {
    return activeDate;
  }

  public MAddress billingAddress() {
    return billingAddress;
  }
}
