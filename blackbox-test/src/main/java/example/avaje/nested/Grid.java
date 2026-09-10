package example.avaje.nested;

import java.util.List;
import java.util.Map;

import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Valid;

@Valid
public record Grid(
    List<List<@NotBlank String>> matrix,
    Map<String, List<@NotBlank String>> tagsByOwner,
    List<Map<String, @NotBlank String>> listOfMaps) {}
