package io.avaje.validation.core;

import io.avaje.validation.adapter.CoreValidation;
import io.avaje.validation.adapter.ValidationRequest;

final class DCoreValidation implements CoreValidation {

    @Override
    public boolean required(Object value, ValidationRequest ctx, String propertyName) {
        if (value == null) {
            ctx.addViolation("Required", propertyName);
            return false;
        }
        return true;
    }
}
