package io.avaje.validation.generator.models.valid;

import java.util.Map;

import io.avaje.lang.Nullable;
import io.avaje.validation.adapter.AnnotationValidator;
import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationRequest;

@AnnotationValidator(Nullable.class)
public final class CustomAnnotationAdapter implements ValidationAdapter<Object> {

  public CustomAnnotationAdapter(ValidationContext ctx, Map<String, Object> attributes) {}

  @Override
  public boolean validate(Object value, ValidationRequest req, String propertyName) {

    return true;
  }
}
