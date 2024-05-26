package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.createSourceFile;
import static io.avaje.validation.generator.ProcessingContext.diAnnotation;

import java.io.IOException;
import java.io.Writer;

import javax.tools.JavaFileObject;

final class SimpleParamBeanWriter {

  private final ValidMethodReader beanReader;
  private final String adapterShortName;
  private final String adapterPackage;
  private final String adapterFullName;
  private Append writer;

  SimpleParamBeanWriter(ValidMethodReader beanReader) {
    this.beanReader = beanReader;
    final var method = beanReader.getBeanType();

    this.adapterPackage = ProcessorUtils.packageOf(method.getEnclosingElement().asType().toString());
    adapterFullName =
        adapterPackage
            + "."
            + method
                .getSimpleName()
                .toString()
                .transform(str -> str.substring(0, 1).toUpperCase() + str.substring(1))
            + "ParamProvider";

    this.adapterShortName = Util.shortName(adapterFullName);
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
    writeMethods();
    writeClassEnd();
    writer.close();
  }

  private void writeImports() {
    beanReader.writeImports(writer);
  }

  private void writePackage() {
    writer.append("package %s;", adapterPackage).eol().eol();
  }

  private void writeClassStart() {
    writer
        .append(
            """
                @Generated("avaje-validator-generator")
                @Named
                @%s
                public final class %s implements MethodAdapterProvider {""",
            Util.shortName(diAnnotation()), adapterShortName)
        .eol();
  }

  private void writeMethods() {
    beanReader.writeValidatorMethod(writer);
  }

  private void writeClassEnd() {
    writer.append("}").eol();
  }
}
