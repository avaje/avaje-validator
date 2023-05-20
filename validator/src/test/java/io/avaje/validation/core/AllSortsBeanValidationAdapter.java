package io.avaje.validation.core;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;
import jakarta.validation.constraints.*;

import java.util.Collections;
import java.util.Map;

public final class AllSortsBeanValidationAdapter implements ValidationAdapter<AllSortsBean> {

  private final ValidationAdapter<String> myNotNull;
  private final ValidationAdapter<String> myNotBlank;
  private final ValidationAdapter<String> myNotEmpty;
  private final ValidationAdapter<String> myEmail;
  private final ValidationAdapter<Boolean> myAssertTrue;
  private final ValidationAdapter<Boolean> myAssertFalse;
  private final ValidationAdapter<String> myNull;
  //private final ValidationAdapter<String> myPattern;

  public AllSortsBeanValidationAdapter(ValidationContext ctx) {
    this.myNotNull = ctx.adapter(NotNull.class, Collections.emptyMap());
    this.myNotBlank = ctx.adapter(NotBlank.class, Collections.emptyMap());
    this.myNotEmpty = ctx.adapter(NotEmpty.class, Collections.emptyMap());
    this.myEmail = ctx.adapter(Email.class, Collections.emptyMap());
    this.myAssertTrue = ctx.adapter(AssertTrue.class, Collections.emptyMap());
    this.myAssertFalse = ctx.adapter(AssertFalse.class, Collections.emptyMap());
    this.myNull = ctx.adapter(Null.class, Collections.emptyMap());
    //this.myPattern = ctx.adapter(Pattern.class, Map.of("regexp", "[0-9]"));
  }

  @Override
  public boolean validate(AllSortsBean pojo, ValidationRequest request, String propertyName) {
    if (propertyName != null) {
      request.pushPath(propertyName);
    }
    myNotNull.validate(pojo.myNotNull, request, "myNotNull");
    myNotBlank.validate(pojo.myNotBlank, request, "myNotBlank");
    myNotEmpty.validate(pojo.myNotEmpty, request, "myNotEmpty");
    myEmail.validate(pojo.myEmail, request, "myEmail");
    myAssertTrue.validate(pojo.myAssertTrue, request, "myAssertTrue");
    myAssertFalse.validate(pojo.myAssertFalse, request, "myAssertFalse");
    myNull.validate(pojo.myNull, request, "myNull");
    //myPattern.validate(pojo.myPattern, request, "myPattern");

    if (propertyName != null) {
      request.popPath();
    }
    return true;
  }
}
