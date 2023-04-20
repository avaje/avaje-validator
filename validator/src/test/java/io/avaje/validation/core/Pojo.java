package io.avaje.validation.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pojo {
  boolean active;
  String name = "";
  LocalDate activeDate;

  public Address billingAddress;
  public Address shippingAddress;

  public List<Contact> contacts = new ArrayList<>();

  public Pojo(boolean active, String name, LocalDate activeDate) {
    this.active = active;
    this.name = name;
    this.activeDate = activeDate;
  }

  public static class Address {

    public String line1;
    public String line2;
    public long longValue;

  }

  public static class Contact {
    public String firstName;
    public String lastName;
  }
}
