package io.avaje.validation.generator.models.valid;

import java.util.Map;
import java.util.Set;

import io.avaje.lang.Nullable;
import io.avaje.validation.adapter.ConstraintValidator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

@ConstraintValidator(Nullable.class)
public final class CustomAnnotationAdapter implements ValidationAdapter<Object> {

  public CustomAnnotationAdapter(ValidationContext ctx, Set<Class<?>> groups, Map<String, Object> attributes) {}

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {

    return true;
  }
}
