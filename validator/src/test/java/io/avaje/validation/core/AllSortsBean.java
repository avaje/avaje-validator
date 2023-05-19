package io.avaje.validation.core;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AllSortsBean {

  @NotNull
  String myNotNull = "valid";

  @NotBlank
  String myNotBlank = "valid";

  @NotEmpty
  String myNotEmpty = "valid";

  @Email
  String myEmail = "valid@foo.com";
}
