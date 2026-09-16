package example.avaje.duplicate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.avaje.validation.Validator;

class DuplicatePropertyBeanTest {

  final Validator validator = Validator.builder().build();

  @Test
  void scalarFieldAndGetter_areValidatedOnceWithPropertyPath() {
    final var bean = new DuplicatePropertyBean();
    bean.setGridId(null);

    assertThat(validator.check(bean)).extracting("path").containsExactly("gridId");
  }

  @Test
  void listFieldAndGetter_areValidatedOnceWithPropertyPath() {
    final var bean = new DuplicatePropertyBean();
    bean.setGridId("grid");
    bean.setGridSections(List.of(new DuplicatePropertyChild()));

    assertThat(validator.check(bean))
        .extracting("path")
        .containsExactly("gridSections[0].id");
  }

  @Test
  void mapFieldAndGetter_areValidatedOnceWithPropertyPath() {
    final var bean = new DuplicatePropertyBean();
    bean.setGridId("grid");
    bean.setItems(Map.of("owner", new DuplicatePropertyChild()));

    assertThat(validator.check(bean))
        .extracting("path")
        .containsExactly("items[owner].id");
  }
}
