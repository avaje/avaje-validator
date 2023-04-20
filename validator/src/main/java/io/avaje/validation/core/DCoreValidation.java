package io.avaje.validation.core;

import io.avaje.validation.adapter.CoreValidation;
import io.avaje.validation.adapter.ValidationRequest;

import java.util.Collection;

final class DCoreValidation implements CoreValidation {

    @Override
    public boolean required(Object value, ValidationRequest ctx, String propertyName) {
        if (value == null) {
            ctx.addViolation("Required", propertyName);
            return false;
        }
        return true;
    }

    @Override
    public boolean collection(ValidationRequest ctx, String propertyName, Collection<?> collection, int minSize, int maxSize) {
        if (collection == null) {
            if (minSize != -1) {
                ctx.addViolation("CollectionNull", propertyName);
            }
            return false;
        }
        final int size = collection.size();
        if (size < minSize) {
            ctx.addViolation("CollectionMinSize", propertyName);
        }
        if (maxSize > 0 && size > maxSize) {
            ctx.addViolation("CollectionMaxSize", propertyName);
        }
        // also validate the collection elements
        return size > 0;
    }
}
