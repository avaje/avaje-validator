package example.avaje.uuid;

import io.avaje.validation.constraints.UUID;
import jakarta.validation.Valid;

@Valid
public record AUuid(
  @UUID
  String str,
  @UUID
  CharSequence charSequence
) {
}
