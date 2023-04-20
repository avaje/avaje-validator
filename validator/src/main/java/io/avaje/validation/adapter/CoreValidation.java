package io.avaje.validation.adapter;

import java.util.Collection;

public interface CoreValidation {

    /** Return true to continue validation on this value if needed */
    boolean optional(ValidationRequest ctx, Object value);

    /** Return true to continue validation on this value if needed */
    boolean required(ValidationRequest ctx, Object value);

    boolean collection(ValidationRequest ctx, Collection<?> collection, int minSize, int maxSize);
}
