package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.createSourceFile;

import java.io.IOException;
import java.io.Writer;

import javax.tools.JavaFileObject;

final class SimpleAdapterWriter {

  private final BeanReader beanReader;
  private final String adapterShortName;
  private final String adapterPackage;
  private final String adapterFullName;
  private final int genericParamsCount;
  private final boolean isContraint;

  private Append writer;

  SimpleAdapterWriter(BeanReader beanReader) {
    this.beanReader = beanReader;
    final AdapterName adapterName = new AdapterName(beanReader.getBeanType());
    this.adapterShortName = adapterName.shortName();
    this.adapterPackage = adapterName.adapterPackage();
    this.adapterFullName = adapterName.fullName();
    this.genericParamsCount = beanReader.genericTypeParamsCount();
    this.isContraint = beanReader instanceof ContraintReader;
  }

  String fullName() {
    return adapterFullName;
  }

  private Writer createFileWriter() throws IOException {
    final JavaFileObject jfo = createSourceFile(adapterFullName);
    return jfo.openWriter();
  }

  void write() throws IOException {
    writer = new Append(createFileWriter());
    writePackage();
    writeImports();
    writeClassStart();
    writeFields();
    writeConstructor();
    writeValidation();
    writeClassEnd();
    writer.close();
  }

  private void writeConstructor() {

    if (isContraint) {
      writer.append("  public %sValidationAdapter(AdapterCreateRequest req", adapterShortName);
    } else {
      writer.append("  public %sValidationAdapter(ValidationContext ctx", adapterShortName);
      for (int i = 0; i < genericParamsCount; i++) {
        writer.append(", Type param%d", i);
      }
    }
    writer.append(") {", adapterShortName).eol();
    beanReader.writeConstructor(writer);
    writer.append("  }").eol();

    if (genericParamsCount > 0) {
      writer.eol();
      writer.append("  /**").eol();
      writer.append("   * Construct using Object for generic type parameters.").eol();
      writer.append("   */").eol();
      writer.append("  public %sValidationAdapter(ValidationContext ctx) {", adapterShortName).eol();
      writer.append("    this(ctx");
      for (int i = 0; i < genericParamsCount; i++) {
        writer.append(", Object.class");
      }
      writer.append(");").eol();
      writer.append("  }").eol();
    }
  }

  private void writeValidation() {
    beanReader.writeValidatorMethod(writer);
  }

  private void writeClassEnd() {
    writer.append("}").eol();
  }

  private void writeClassStart() {
    writer.append("@Generated(\"avaje-validator-generator\")").eol();
    if (isContraint) {
      writer.append("@ConstraintAdapter(%s.class)", beanReader.contraintTarget()).eol();
    }

    writer.append("public final %sclass %sValidationAdapter implements ValidationAdapter<%s> ", Util.valhalla(), adapterShortName, beanReader.shortName());
    writer.append("{").eol().eol();
  }

  private void writeFields() {
    beanReader.writeFields(writer);
  }

  private void writeImports() {
    beanReader.writeImports(writer, adapterPackage);
  }

  private void writePackage() {
    writer.append("package %s;", adapterPackage).eol().eol();
  }
}
