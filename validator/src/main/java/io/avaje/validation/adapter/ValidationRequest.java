package io.avaje.validation.adapter;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;

import java.util.*;

public class ValidationRequest {

    private final ArrayDeque<String> pathStack = new ArrayDeque<>();

    private final Set<ConstraintViolation> violations = new LinkedHashSet<>();


    public void addViolation(String msg) {
        violations.add(new ConstraintViolation(currentPath(), msg));
    }

    private String currentPath() {
        StringJoiner joiner = new StringJoiner(".");
        for (String next : pathStack) {
            joiner.add(next);
        }
        return joiner.toString();
    }


    public void pushPath(String path) {
        pathStack.push(path);
    }

    public void popPath() {
        pathStack.pop();
    }

    public void throwWithViolations() {
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
