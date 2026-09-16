package example.avaje.payload;

import jakarta.validation.Payload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Valid
public class JakartaPayload {

  public interface Severity extends Payload {
    interface Error extends Severity {}
  }

  @NotBlank(payload = {Severity.Error.class})
  final String name;

  public JakartaPayload(String name) {
    this.name = name;
  }
}
