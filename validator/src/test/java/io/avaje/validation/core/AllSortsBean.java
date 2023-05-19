package io.avaje.validation.core;

import jakarta.validation.constraints.*;

public class AllSortsBean {

  @NotNull
  String myNotNull = "valid";

  @NotBlank
  String myNotBlank = "valid";

  @NotEmpty
  String myNotEmpty = "valid";

  @Email
  String myEmail = "valid@foo.com";

  @AssertTrue
  boolean myAssertTrue = true;

  @AssertFalse
  boolean myAssertFalse = false;
}
