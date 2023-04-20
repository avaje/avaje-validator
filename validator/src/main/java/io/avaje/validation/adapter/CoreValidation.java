package io.avaje.validation.adapter;

import java.util.Collection;

public interface CoreValidation {

    /** Return true to continue validation on this value if needed */
    boolean required(Object value, ValidationRequest ctx, String propertyName);

    boolean collection(ValidationRequest ctx, String propertyName, Collection<?> collection, int minSize, int maxSize);
}
