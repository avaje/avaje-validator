package example.avaje.length;

import io.avaje.validation.constraints.Length;
import jakarta.validation.Valid;

@Valid
public record ALength(
  @Length(min = 1, max = 3)
  String both,
  @Length(max = 5)
  String onlyMax,
  @Length(min = 2)
  String onlyMin,
  @Length(min = 2, max = 10, message = "Custom length message")
  String withMsg
) {
}
