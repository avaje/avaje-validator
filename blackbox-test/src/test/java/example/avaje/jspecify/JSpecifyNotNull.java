package example.avaje.jspecify;

import org.jspecify.annotations.Nullable;

import jakarta.validation.Valid;

@Valid
public record JSpecifyNotNull(String basic, String withMax, @Nullable String withCustom) {

  /**
   * Void method not included in validation.
   */
  public void method() {}


  public String describe() {
    return null;
  }

}
