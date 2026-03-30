package example.avaje.inherit;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotNull;
import io.avaje.validation.constraints.Valid;

@Valid
public class ConcreteWithGenericBase extends GenericBase<ConcreteWithGenericBase.InnerConfig> {

  public ConcreteWithGenericBase(InnerConfig config) {
    super(config);
  }

  public static class InnerConfig {

    @NotBlank public String value;

    @NotNull public String code;

    public InnerConfig(String value, String code) {
      this.value = value;
      this.code = code;
    }
  }
}
