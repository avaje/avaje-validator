package example.avaje.mixin;

import example.avaje.mixin.Captain.Bankai;
import io.avaje.lang.Nullable;
import io.avaje.validation.MixIn;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Positive;
import jakarta.validation.Valid;

@MixIn(Captain.class)
public abstract class CaptainMixin {

  @NotBlank private String name;

  // disables validation (kenpachi existed for a while)
  private Bankai bankai;

  @Valid
  @Nullable
  public abstract Bankai bankai();

  @MixIn(Bankai.class)
  public record BankaiMixin(@NotBlank String name, @Positive int forceMultiplier) {}
}
