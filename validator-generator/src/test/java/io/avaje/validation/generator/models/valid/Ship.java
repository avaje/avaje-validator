package io.avaje.validation.generator.models.valid;

import java.util.List;
import java.util.Map;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;
import io.avaje.validation.constraints.Valid;

@Valid
public record Ship(
    @NotEmpty(message = "sus ", groups = Ship.class)
        Map<
                @NotEmpty(groups = Ship.class) @NotBlank String,
                @NotBlank(groups = Ship.class) @Valid CrewMate>
            crew,
    @NotEmpty(message = "tasks,=(testing wierd chars&rparen; ")
        List<
                @NotEmpty(groups = List.class)
                @NotBlank(groups = Ship.class, message = "tasks,=(testing wierd chars&rparen; ")
                String>
            tasks) {}
