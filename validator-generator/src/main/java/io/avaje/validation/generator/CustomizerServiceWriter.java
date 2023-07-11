package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.createMetaInfWriterFor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementFilter;

public class CustomizerServiceWriter {

  private Append writer;
  private boolean eol;

  CustomizerServiceWriter() {
    try {
      final var jfo = createMetaInfWriterFor(Constants.META_INF_CUSTOMIZER);
      writer = new Append(jfo.openWriter());
    } catch (final IOException e) {
      e.printStackTrace();
      throw new UncheckedIOException(e);
    }
  }

  void writeMetaInf(Set<? extends Element> set) {
    for (final var element : ElementFilter.typesIn(set)) {
      final var fqn = element.getQualifiedName().toString();
      if (eol) {
        writer.eol().append(fqn);
      } else {
        writer.append(fqn);
        eol = true;
      }
    }
  }

  void close() {
    writer.close();
  }
}
