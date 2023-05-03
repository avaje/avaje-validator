package io.avaje.validation.adapter;

import io.avaje.validation.ConstraintViolation;
import io.avaje.validation.ConstraintViolationException;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

public interface ValidationRequest {

    static ValidationRequest create() {
        return new DRequest();
    }

    void addViolation(String msg, String propertyName);

    void pushPath(String path);

    void popPath();

    void throwWithViolations();

    class DRequest implements ValidationRequest {

        private final ArrayDeque<String> pathStack = new ArrayDeque<>();

        private final Set<ConstraintViolation> violations = new LinkedHashSet<>();


        private String currentPath() {
            StringJoiner joiner = new StringJoiner(".");
            final var descendingIterator = pathStack.descendingIterator();
            while (descendingIterator.hasNext()) {
                joiner.add(descendingIterator.next());
            }
            return joiner.toString();
        }


        @Override
        public void addViolation(String msg, String propertyName) {
            violations.add(new ConstraintViolation(currentPath(), propertyName, msg));
        }

        @Override
        public void pushPath(String path) {
            pathStack.push(path);
        }

        @Override
        public void popPath() {
            pathStack.pop();
        }

        @Override
        public void throwWithViolations() {
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
    }
}
