package example.avaje.crossfield;

import example.avaje.otherannotation.MyJson;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@MyJson // @MyJson is not a constraint
@Valid
public record NotClassLevel(
  @NotBlank String name
) {
}
