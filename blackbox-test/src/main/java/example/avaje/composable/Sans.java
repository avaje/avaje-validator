package example.avaje.composable;

import jakarta.validation.Valid;

@Valid
public record Sans(
    @SansPositiveContraint(message = "must have positive double digit amount of puns") int puns) {}
