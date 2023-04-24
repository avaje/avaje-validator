package io.avaje.validation.adapter;

public interface CoreValidation {

    /** Return true to continue validation on this value if needed */
    boolean required(Object value, ValidationRequest ctx, String propertyName);
}
