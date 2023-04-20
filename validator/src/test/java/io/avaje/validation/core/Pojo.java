package io.avaje.validation.core;

import java.time.LocalDate;

public class Pojo {
  boolean bool = false;
  String str = "";
  LocalDate date;

  public Pojo(boolean bool, String str, LocalDate date) {
    this.bool = bool;
    this.str = str;
    this.date = date;
  }
}
