package example.avaje.payload;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Payload;
import jakarta.validation.Valid;

@Valid
public class PayloadBean {

  public interface Severity extends Payload {
    interface Error extends Severity {}

    interface Warning extends Severity {}
  }

  @NotBlank(payload = {Severity.Error.class})
  final String name;

  public PayloadBean(String name) {
    this.name = name;
  }
}
