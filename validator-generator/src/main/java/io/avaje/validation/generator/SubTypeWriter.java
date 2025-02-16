package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.createSourceFile;
import static io.avaje.validation.generator.APContext.logError;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import io.jstach.jstache.JStache;

public class SubTypeWriter {

  TypeElement element;
  List<String> subtypeStrings;
  private final Set<String> importTypes = new TreeSet<>();
  private final String shortName;
  private final String adapterPackage;
  private final String adapterFullName;
  private final String shortType;

  public SubTypeWriter(TypeElement element, List<TypeMirror> subtypes) {
    this.element = element;
    this.subtypeStrings =
        subtypes.stream().map(TypeMirror::toString).map(ProcessorUtils::shortType).toList();
    final AdapterName adapterName = new AdapterName(element);
    this.shortName = adapterName.shortName();
    this.adapterPackage = adapterName.adapterPackage();
    this.adapterFullName = adapterName.fullName();
    this.shortType = element.getQualifiedName().toString().transform(ProcessorUtils::shortType);

    importTypes.add("io.avaje.validation.adapter.ValidationAdapter");
    importTypes.add("io.avaje.validation.adapter.ValidationContext");
    importTypes.add("io.avaje.validation.adapter.ValidationRequest");
    importTypes.add("io.avaje.validation.spi.Generated");
    subtypes.stream().map(TypeMirror::toString).forEach(importTypes::add);
  }

  private Writer createFileWriter() throws IOException {
    final JavaFileObject jfo = createSourceFile(adapterFullName);
    return jfo.openWriter();
  }

  void write() {
    Append writer;
    try {
      writer = new Append(createFileWriter());
      var validation =
          APContext.jdkVersion() > 17
              ? new SwitchValidation(
                  subtypeStrings, element.getModifiers().contains(Modifier.SEALED))
              : new IfValidation(subtypeStrings);

      var template =
          new SubTemplate(
                  adapterPackage, importTypes, shortName, shortType, subtypeStrings, validation)
              .render();

      writer.append(template).close();
    } catch (IOException e) {

      logError("Error writing ValidationAdapter for %s %s", element, e);
    }
  }

  String fullName() {
    return adapterFullName;
  }

  @JStache(
      template =
          """
      package {{packageName}};

      {{#imports}}
      import {{.}};
      {{/imports}}

      @Generated("avaje-validation-generator")
      public class {{shortName}}ValidationAdapter implements ValidationAdapter<{{shortType}}> {

      {{#subtypes}}
        private final ValidationAdapter<{{.}}> subAdapter{{@index}};
      {{/subtypes}}

        public {{shortName}}ValidationAdapter(ValidationContext ctx) {
      {{#subtypes}}
          this.subAdapter{{@index}} = ctx.adapter({{.}}.class);
      {{/subtypes}}
        }

        @Override
        public boolean validate({{shortType}} value, ValidationRequest request, String field) {
      {{validation.render}}
        }
      }
      """)
  public record SubTemplate(
      String packageName,
      Set<String> imports,
      String shortName,
      String shortType,
      List<String> subtypes,
      Template validation) {
    String render() {
      return SubTemplateRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """
        {{#subtypes}}
        if (value instanceof {{.}} val) {
          return subAdapter{{@index}}.validate(val, request, field);
        }
        {{/subtypes}}
        return true;
    """)
  public record IfValidation(List<String> subtypes) implements Template {
    @Override
    public String render() {
      return IfValidationRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """
    return switch(value) {
      case null -> true;
    {{#subtypes}}
      case {{.}} val -> subAdapter{{@index}}.validate(val, request, field);
    {{/subtypes}}
    {{^sealed}}
      default -> true;
    {{/sealed}}
    };
""")
  public record SwitchValidation(List<String> subtypes, boolean sealed) implements Template {
    @Override
    public String render() {
      return SwitchValidationRenderer.of().execute(this);
    }
  }

  interface Template {
    String render();
  }
}
