package example.avaje.inherit;

import io.avaje.validation.constraints.Valid;

public abstract class GenericBase<T> {
  @Valid private T config;

  public GenericBase(T config) {
    this.config = config;
  }

  public T getConfig() {
    return config;
  }

  public void setConfig(T config) {
    this.config = config;
  }
}
