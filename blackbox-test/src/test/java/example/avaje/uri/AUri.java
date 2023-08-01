package example.avaje.uri;

import io.avaje.validation.constraints.URI;
import jakarta.validation.Valid;

@Valid
public record AUri(
  @URI(scheme="http", host = "localhost")
  String str,
  @URI(port=81)
  CharSequence charSequence,
  @URI(regexp="(https:.*)")
  String withReg
) {
}
