package io.avaje.validation;

import java.util.Set;

public final class ConstraintViolationException extends RuntimeException {

    private final Set<ConstraintViolation> violations;

    public ConstraintViolationException(Set<ConstraintViolation> violations) {
        this.violations = violations;
    }

    public Set<ConstraintViolation> violations() {
        return violations;
    }
}
