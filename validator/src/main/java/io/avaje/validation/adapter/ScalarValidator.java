package io.avaje.validation.adapter;

public interface ScalarValidator<T> {

    /** Return true to continue validation on this value if needed */
    boolean optional(ValidationRequest ctx, T value);

    /** Return true to continue validation on this value if needed */
    boolean required(ValidationRequest ctx, T value);

    /** Return true to continue validation on this value if needed */
    boolean optional(ValidationRequest ctx, T value, int minLength, int maxLength);

    /** Return true to continue validation on this value if needed */
    boolean required(ValidationRequest ctx, T value, int minLength, int maxLength);

    boolean min(ValidationRequest ctx, T value, T minValue);
    boolean max(ValidationRequest ctx, T value, T maxValue);
    boolean range(ValidationRequest ctx, T value, T minValue, int maxValue);
}
