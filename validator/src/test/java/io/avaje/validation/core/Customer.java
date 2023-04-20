package io.avaje.validation.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer {
  boolean active;
  String name = "";
  LocalDate activeDate;

  public Address billingAddress;
  public Address shippingAddress;

  public List<Contact> contacts = new ArrayList<>();

  public Customer(boolean active, String name, LocalDate activeDate) {
    this.active = active;
    this.name = name;
    this.activeDate = activeDate;
  }

}
