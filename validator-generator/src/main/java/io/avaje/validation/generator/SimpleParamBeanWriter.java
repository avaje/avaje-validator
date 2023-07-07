package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.createWriter;

import java.io.IOException;
import java.io.Writer;

import javax.tools.JavaFileObject;

final class SimpleParamBeanWriter {

  private final ValidMethodReader beanReader;
  private final String adapterShortName;
  private final String adapterPackage;
  private final String adapterFullName;
  private Append writer;
  private static boolean writeAspect = true;

  SimpleParamBeanWriter(ValidMethodReader beanReader) {
    this.beanReader = beanReader;
    final var method = beanReader.getBeanType();

    this.adapterPackage = Util.packageOf(method.getEnclosingElement().asType().toString());
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
    final JavaFileObject jfo = createWriter(adapterFullName);
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
    beanReader.writeImports(writer, writeAspect);
  }

  private void writePackage() {
    writer.append("package %s;", adapterPackage).eol().eol();
  }

  private void writeClassStart() {
    writer.append("@Generated").eol();
    writer.append("@Component").eol();
    if (writeAspect) {
      writer.append("@Component.Import(AOPMethodValidator.class)").eol();
      writeAspect = false;
    }

    writer.append("public final class %s implements MethodAdapterProvider", adapterShortName);
    writer.append("{").eol().eol();
  }

  private void writeMethods() {
    beanReader.writeValidatorMethod(writer);
  }

  private void writeClassEnd() {
    writer.append("}").eol();
  }
}
