package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.createSourceFile;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.tools.JavaFileObject;

final class SimpleComponentWriter {

  private final ComponentMetaData metaData;
  private final Set<String> importTypes = new TreeSet<>();
  private Append writer;
  private JavaFileObject fileObject;
  private String fullName;
  private String packageName;

  SimpleComponentWriter(ComponentMetaData metaData) {
    this.metaData = metaData;
  }

  void initialise(boolean pkgPrivate) throws IOException {
    fullName = metaData.fullName(pkgPrivate);
    packageName =
        "GeneratedValidatorComponent".equals(fullName) ? "" : ProcessorUtils.packageOf(fullName);
    if (fileObject == null) {
      fileObject = createSourceFile(fullName);
    }
    if (!metaData.isEmpty()) {
      ProcessingContext.addValidatorSpi(fullName);
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
    final String shortName = Util.shortName(fullName);
    writer.append("@Generated(\"avaje-validator-generator\")").eol();
    final List<String> factories = metaData.allFactories();
    final List<String> annotationFactories = metaData.allAnnotationAdapters().stream()
      .map(s -> s.getQualifiedName().toString())
      .toList();
    if (!factories.isEmpty()) {
      writer.append("@MetaData.ValidFactory({");
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

    writer.append("public %sclass %s implements GeneratedComponent {", Util.valhalla(), shortName).eol().eol();
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
      if (Util.validImportType(importType, packageName)) {
        writer.append("import %s;", importType).eol();
      }
    }
    writer.eol();
  }

  private void writePackage() {
    if (packageName != null && !packageName.isEmpty()) {
      writer.append("package %s;", packageName).eol().eol();
    }
  }
}
