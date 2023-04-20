package io.avaje.validation.core;

import io.avaje.validation.adapter.CoreValidation;
import io.avaje.validation.adapter.ValidationRequest;

import java.util.Collection;

final class DCoreValidation implements CoreValidation {
    @Override
    public boolean optional(ValidationRequest ctx, Object value) {
        return value != null;
    }

    @Override
    public boolean required(ValidationRequest ctx, Object value) {
        if (value == null) {
            ctx.addViolation("Required");
            return false;
        }
        return true;
    }

    @Override
    public boolean collection(ValidationRequest ctx, Collection<?> collection, int minSize, int maxSize) {
        if (collection == null) {
            ctx.addViolation("CollectionNull");
            return false;
        }
        final int size = collection.size();
        if (size < minSize) {
            ctx.addViolation("CollectionMinSize");
        }
        if (maxSize > 0 && size > maxSize) {
            ctx.addViolation("CollectionMaxSize");
        }
        // also validate the collection elements
        return true;
    }
}
