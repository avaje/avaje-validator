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
      ProcessingContext.addValidatorSpi(name);
      ProcessingContext.validateModule();
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
    var services = ProcessingContext.readExistingMetaInfServices();
    final FileObject fileObject = createMetaInfWriterFor(Constants.META_INF_COMPONENT);
    if (fileObject != null) {
      try (Writer writer = fileObject.openWriter()) {
        services.add(metaData.fullName());
        writer.write(String.join("\n", services));
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
      final String typeName = adapterFullName
        .transform(Util::baseTypeOfAdapter)
        .transform(ProcessorUtils::shortType);
      writer.append("    builder.add(%s.class, %s::new);", typeName, adapterShortName).eol();
    }

    for (final var adapter : metaData.allAnnotationAdapters()) {
      final var typeShortName = adapter.getQualifiedName()
        .toString()
        .transform(ProcessorUtils::shortType);
      final var target = ConstraintAdapterPrism.getInstanceOn(adapter)
        .value()
        .toString()
        .transform(ProcessorUtils::shortType);

      writer.append("    builder.add(%s.class, %s::new);", target, typeShortName).eol();
    }

    writer.append("  }").eol().eol();
  }

  private void writeClassEnd() {
    writer.append("}").eol();
  }

  private void writeClassStart() {
    final String fullName = metaData.fullName();
    final String shortName = Util.shortName(fullName);
    writer.append("@Generated(\"avaje-validator-generator\")").eol();
    final List<String> factories = metaData.allFactories();
    final List<String> annotationFactories = metaData.allAnnotationAdapters().stream()
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
    importTypes.add("io.avaje.validation.spi.GeneratedComponent");
    importTypes.add("io.avaje.validation.spi.MetaData");
    importTypes.add("io.avaje.validation.spi.Generated");
    importTypes.addAll(metaData.allImports());

    for (final String importType : importTypes) {
      if (Util.validImportType(importType, metaData.packageName())) {
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
