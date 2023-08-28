package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.createMetaInfWriterFor;
import static io.avaje.validation.generator.APContext.createSourceFile;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;

final class SimpleComponentWriter {

  private final ComponentMetaData metaData;
  private final Set<String> importTypes = new TreeSet<>();
  private Append writer;
  private JavaFileObject fileObject;

  SimpleComponentWriter(ComponentMetaData metaData) {
    this.metaData = metaData;
  }

  void initialise() throws IOException {
    var name = metaData.fullName();
    if (fileObject == null) {
      fileObject = createSourceFile(name);
    }
    if (!metaData.isEmpty()) {
      ProcessingContext.validateModule(name);
    }
  }

  private Writer createFileWriter() throws IOException {
    return fileObject.openWriter();
  }

  void write() throws IOException {
    writer = new Append(createFileWriter());
    writePackage();
    writeImports();
    writeClassStart();
    writeRegister();
    writeClassEnd();
    writer.close();
  }

  void writeMetaInf() throws IOException {
    final FileObject fileObject = createMetaInfWriterFor(Constants.META_INF_COMPONENT);
    if (fileObject != null) {
      try (Writer writer = fileObject.openWriter()) {
        writer.write(metaData.fullName());
      }
    }
  }

  private void writeRegister() {
    writer.append("  @Override").eol();
    writer.append("  public void customize(Validator.Builder builder) {").eol();
    final List<String> strings = metaData.allFactories();
    for (final String adapterFullName : strings) {
      final String adapterShortName = Util.shortName(adapterFullName);
      writer.append("    builder.add(%s.Factory);", adapterShortName).eol();
    }
    for (final String adapterFullName : metaData.all()) {
      final String adapterShortName = Util.shortName(adapterFullName);
      final String typeName =
          adapterFullName
              .transform(Util::baseTypeOfAdapter)
              .transform(Util::shortName)
              .transform(this::typeShortName);
      writer.append("    builder.add(%s.class, %s::new);", typeName, adapterShortName).eol();
    }

    for (final var adapter : metaData.allAnnotationAdapters()) {
      final var typeShortName =
          adapter
              .getQualifiedName()
              .toString()
              .transform(Util::shortType)
              .transform(this::typeShortName);

      final var target =
          ConstraintAdapterPrism.getInstanceOn(adapter)
              .value()
              .toString()
              .transform(Util::shortType)
              .transform(this::typeShortName);

      writer.append("    builder.add(%s.class, %s::new);", target, typeShortName).eol();
    }

    writer.append("  }").eol().eol();
  }

  private String typeShortName(String adapterShortName) {
    return adapterShortName.replace("$", ".");
  }

  private void writeClassEnd() {
    writer.append("}").eol();
  }

  private void writeClassStart() {
    final String fullName = metaData.fullName();
    final String shortName = Util.shortName(fullName);
    writer.append("@Generated").eol();
    final List<String> factories = metaData.allFactories();
    final List<String> annotationFactories =
        metaData.allAnnotationAdapters().stream()
            .map(s -> s.getQualifiedName().toString())
            .toList();
    if (!factories.isEmpty()) {
      writer.append("@MetaData.Factory({");
      writeMetaDataEntry(annotationFactories);
      writer.append("})").eol();
    }
    if (!annotationFactories.isEmpty()) {
      writer.append("@MetaData.AnnotationFactory({");
      writeMetaDataEntry(annotationFactories);
      writer.append("})").eol();
    }
    writer.append("@MetaData({");
    final List<String> all = metaData.all();
    writeMetaDataEntry(all);
    writer.append("})").eol();

    writer.append("public class %s implements GeneratedComponent {", shortName).eol().eol();
  }

  private void writeMetaDataEntry(List<String> entries) {
    for (int i = 0, size = entries.size(); i < size; i++) {
      if (i > 0) {
        writer.append(", ");
      }
      writer.append("%s.class", Util.shortName(entries.get(i)));
    }
  }

  private void writeImports() {
    importTypes.add(Constants.VALIDATOR);
    importTypes.add(Constants.VALID_SPI);
    importTypes.add("io.avaje.validation.Validator.GeneratedComponent");
    importTypes.addAll(metaData.allImports());

    for (final String importType : importTypes) {
      if (Util.validImportType(importType)) {
        writer.append("import %s;", importType).eol();
      }
    }
    writer.eol();
  }

  private void writePackage() {
    final String packageName = metaData.packageName();
    if (packageName != null && !packageName.isEmpty()) {
      writer.append("package %s;", packageName).eol().eol();
    }
  }
}
