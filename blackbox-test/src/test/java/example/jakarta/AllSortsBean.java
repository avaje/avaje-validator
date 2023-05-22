package example.jakarta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Valid
public class AllSortsBean {

  @NotNull
  public String myNotNull = "valid";

  @NotBlank
  public String myNotBlank = "valid";

  @NotEmpty
  public String myNotEmpty = "valid";

  @Email
  public String myEmail = "valid@foo.com";

  @AssertTrue
  public boolean myAssertTrue = true;

  @AssertFalse
  public boolean myAssertFalse = false;

  @Null
  public String myNull = null;

}
