package org.example;


import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public class Customer {

  @NotBlank
  final String name;
  @NotBlank
  final String email;

  public Customer(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public String name() {
    return name;
  }

  public String email() {
    return email;
  }
}
