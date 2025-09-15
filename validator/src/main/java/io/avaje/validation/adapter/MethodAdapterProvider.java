package io.avaje.validation.adapter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Provides adapters for method validation, including parameter, return, and cross-parameter adapters.
 * <p>
 * Implementations supply the target {@link Method} and adapters for validating method parameters,
 * return values, and cross-parameter constraints within a validation context.
 */
public interface MethodAdapterProvider {

  /**
   * Returns the target method to be validated.
   *
   * @return the {@link Method} instance
   * @throws Exception if the method cannot be provided
   */
  Method method() throws Exception;

  /**
   * Provides adapters for validating each method parameter.
   *
   * @param ctx the validation context
   * @return list of parameter adapters
   */
  List<ValidationAdapter<Object>> paramAdapters(ValidationContext ctx);

  /**
   * Provides an adapter for validating the method return value.
   * Default implementation returns a no-op adapter.
   *
   * @param ctx the validation context
   * @return the return value adapter
   */
  default ValidationAdapter<Object> returnAdapter(ValidationContext ctx) {
    return ctx.noop();
  }

  /**
   * Provides an adapter for cross-parameter validation (constraints involving multiple parameters).
   * Default implementation returns a no-op adapter.
   *
   * @param ctx the validation context
   * @return the cross-parameter adapter
   */
  default ValidationAdapter<Object[]> crossParamAdapter(ValidationContext ctx) {
    return ctx.noop();
  }

  /**
   * Provides the target method, wrapping checked exceptions as {@link IllegalStateException}.
   *
   * @return the {@link Method} instance
   * @throws IllegalStateException if the method cannot be provided
   */
  default Method provide() {
    try {
      return method();
    } catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
