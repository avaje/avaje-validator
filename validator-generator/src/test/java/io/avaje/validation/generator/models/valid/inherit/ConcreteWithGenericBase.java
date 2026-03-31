package io.avaje.validation.generator.models.valid.inherit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Valid
public class ConcreteWithGenericBase extends GenericBase<ConcreteWithGenericBase.InnerConfig> {

  public ConcreteWithGenericBase(InnerConfig config) {
    super(config);
  }

  @Valid
  public static class InnerConfig {

    @NotBlank public String value;

    @NotNull public String code;
  }
}
