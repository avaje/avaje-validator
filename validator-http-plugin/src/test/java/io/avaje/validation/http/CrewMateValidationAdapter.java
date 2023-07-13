package io.avaje.validation.http;

import java.util.Map;

import io.avaje.validation.adapter.ValidationAdapter;
import io.avaje.validation.adapter.ValidationContext;
import io.avaje.validation.adapter.ValidationContext.Message;
import io.avaje.validation.adapter.ValidationRequest;
import io.avaje.validation.spi.Generated;

@Generated
public final class CrewMateValidationAdapter implements ValidationAdapter<CrewMate> {

  private final Message message;

  public CrewMateValidationAdapter(ValidationContext ctx) {
    this.message = ctx.message("ඞ", Map.of());
  }

  @Override
  public boolean validate(CrewMate value, ValidationRequest request, String propertyName) {
    if (value.assignedTasks().isBlank()) {
      request.addViolation(message, propertyName);
    }
    return true;
  }
}
