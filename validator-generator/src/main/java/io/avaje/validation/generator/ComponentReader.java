package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.typeElement;
import static io.avaje.validation.generator.APContext.filer;
import static io.avaje.validation.generator.APContext.logNote;
import static io.avaje.validation.generator.APContext.logWarn;

import java.io.FileNotFoundException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.FilerException;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

final class ComponentReader {

  private final ComponentMetaData componentMetaData;

  ComponentReader(ComponentMetaData metaData) {
    this.componentMetaData = metaData;
  }

  void read() {
    final String componentFullName = loadMetaInfServices();
    if (componentFullName != null) {
      final TypeElement moduleType = typeElement(componentFullName);
      if (moduleType != null) {
        componentMetaData.setFullName(componentFullName);
        readMetaData(moduleType);
      }
    }
  }

  /** Read the existing JsonAdapters from the MetaData annotation of the generated component. */
  private void readMetaData(TypeElement moduleType) {
    for (final AnnotationMirror annotationMirror : moduleType.getAnnotationMirrors()) {
      final MetaDataPrism metaData = MetaDataPrism.getInstance(annotationMirror);
      final FactoryPrism metaDataFactory = FactoryPrism.getInstance(annotationMirror);
      final AnnotationFactoryPrism metaDataAnnotationFactory = AnnotationFactoryPrism.getInstance(annotationMirror);

      if (metaData != null) {
        metaData.value().stream().map(TypeMirror::toString).forEach(componentMetaData::add);

      } else if (metaDataFactory != null) {
        metaDataFactory.value().stream().map(TypeMirror::toString).forEach(componentMetaData::add);

      } else if (metaDataAnnotationFactory != null) {
        metaDataAnnotationFactory.value().stream()
            .map(APContext::asTypeElement)
            .forEach(componentMetaData::addAnnotationAdapter);
      }
    }
  }

  private String loadMetaInfServices() {
    final List<String> lines = loadMetaInf();
    return lines.isEmpty() ? null : lines.get(0);
  }

  private List<String> loadMetaInf() {
    try {
      final FileObject fileObject =
    		  filer()
              .getResource(StandardLocation.CLASS_OUTPUT, "", Constants.META_INF_COMPONENT);

      if (fileObject != null) {
        final List<String> lines = new ArrayList<>();
        final Reader reader = fileObject.openReader(true);
        final LineNumberReader lineReader = new LineNumberReader(reader);
        String line;
        while ((line = lineReader.readLine()) != null) {
          line = line.trim();
          if (!line.isEmpty()) {
            lines.add(line);
          }
        }
        return lines;
      }

    } catch (FileNotFoundException | NoSuchFileException e) {
      // logDebug("no services file yet");

    } catch (final FilerException e) {
      logNote("FilerException reading services file");

    } catch (final Exception e) {
      e.printStackTrace();
      logWarn("Error reading services file: " + e.getMessage());
    }
    return Collections.emptyList();
  }
}
