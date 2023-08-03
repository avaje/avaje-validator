package io.avaje.validation.generator.models.valid.typeconstraint;

import io.avaje.validation.constraints.Valid;

@Valid
@PassingSkill
public record Tarnished(int vigor, int endurance) {}
