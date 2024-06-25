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
import java.util.Objects;

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
    ProcessingContext.readExistingMetaInfServices().stream()
        .map(APContext::typeElement)
        .filter(Objects::nonNull)
        .filter(
            t -> "io.avaje.validation.spi.GeneratedComponent".equals(t.getSuperclass().toString()))
        .findFirst()
        .ifPresent(
            moduleType -> {
              if (moduleType != null) {
                componentMetaData.setFullName(moduleType.getQualifiedName().toString());
                readMetaData(moduleType);
              }
            });
  }

  /** Read the existing adapters from the MetaData annotation of the generated component. */
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
}
