package example.avaje.range;

import io.avaje.validation.constraints.Range;
import jakarta.validation.Valid;

import java.math.BigInteger;

@Valid
public record ABigIntRange(
  @Range(min = 1, max = 3)
  BigInteger decimal
) {
}
