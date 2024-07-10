package io.avaje.validation.generator.models.valid;

import io.avaje.validation.MixIn;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.generator.models.valid.Captain.Bankai;
import io.avaje.validation.generator.models.valid.typeconstraint.FraudWatch;
import jakarta.validation.Valid;

@FraudWatch
@MixIn(Captain.class)
public abstract class CaptainMixin {

  @NotBlank private String name;

  // disables validation (kenpachi existed for a while)
  private Bankai bankai;

  @Valid
  public abstract Bankai bankai();

  @MixIn(Bankai.class)
  public record BankaiMixin(@NotBlank String name) {}
}
