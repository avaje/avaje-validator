package io.avaje.validation.generator.models.valid;

import java.util.List;
import java.util.Map;

import io.avaje.validation.ValidPojo;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;

@ValidPojo
public record Ship(
    @NotEmpty(message = "sus ")
        Map<@NotEmpty(groups = Ship.class) @NotBlank String, @NotBlank(groups = Ship.class) CrewMate> crew,
    @NotEmpty(message = "sus ")
        List<@NotEmpty(groups = Ship.class) @NotBlank(groups = Ship.class) String> tasks) {}
