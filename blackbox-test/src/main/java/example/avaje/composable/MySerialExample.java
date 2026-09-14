package example.avaje.composable;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Payload;
import io.avaje.validation.constraints.Valid;

@Valid
public record MySerialExample(
  @MySerial(payload = MySerialExample.Severity.Error.class) String key,
  @NotBlank String value) {

  public interface Severity extends Payload {
    interface Error extends Severity {}
  }
}
