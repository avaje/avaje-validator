package io.avaje.validation.generator;

import java.io.IOException;
import java.io.Writer;

/** Helper that wraps a writer with some useful methods to append content. */
final class Append {

  private static final boolean debug = Boolean.getBoolean("append.debug");
  private final Writer writer;
  private final StringBuilder stringBuilder = new StringBuilder();

  Append(Writer writer) {
    this.writer = writer;
  }

  Append append(String content) {
    try {
      String replace = content.replace("\"groups\",List.of(", "\"groups\",Set.of(");
      writer.append(replace);

      if (debug) {
        stringBuilder.append(replace);
      }
      return this;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  void close() {
    try {
      writer.flush();
      writer.close();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  Append eol() {
    try {
      writer.append("\n");
      if (debug) {
        stringBuilder.append("\n");
      }
      return this;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Append content with formatted arguments. */
  Append append(String format, Object... args) {
    return append(String.format(format, args));
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}
