package io.avaje.validation.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pojo {
  boolean bool = false;
  String str = "";
  LocalDate date;

  public String firstName;
  public String lastName;
  public LocalDate activeDate;

  public Address billingAddress;
  public Address shippingAddress;

  public List<Contact> contacts = new ArrayList<>();

  public Pojo(boolean bool, String str, LocalDate date) {
    this.bool = bool;
    this.str = str;
    this.date = date;
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
