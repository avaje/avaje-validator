package io.avaje.validation.core;

import io.avaje.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Valid
public class Customer {
  boolean active;
  String name = "";
  LocalDate activeDate;

  // Required | NotNull
  public Address billingAddress;
  // Optional | Nullable
  public Address shippingAddress;

  @Size(min = 0, max = 2)
  public List<Contact> contacts = new ArrayList<>();

  public Customer(boolean active, String name, LocalDate activeDate) {
    this(active, name, activeDate, "line1");
  }

  public Customer(boolean active, String name, LocalDate activeDate, String line1) {
    this.active = active;
    this.name = name;
    this.activeDate = activeDate;
    if (line1 != null) {
      this.billingAddress = new Address();
      this.billingAddress.line1 = line1;
    }
  }

}
