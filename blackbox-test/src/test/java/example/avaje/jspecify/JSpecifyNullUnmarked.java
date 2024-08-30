package example.avaje.jspecify;

import org.jspecify.annotations.NullUnmarked;

import io.avaje.validation.constraints.Null;
import jakarta.validation.Valid;

@Valid
@NullUnmarked
public record JSpecifyNullUnmarked(@Null String basic) {}
