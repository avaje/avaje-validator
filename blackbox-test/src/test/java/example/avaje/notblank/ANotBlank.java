package example.avaje.notblank;

import io.avaje.validation.constraints.NotBlank;
import jakarta.validation.Valid;

@Valid
public record ANotBlank(
  @NotBlank
  String basic,
  @NotBlank(max = 4)
  String withMax,

  @NotBlank(max = 4, message = "NotBlank n max 4")
  String withCustom
) {
}
