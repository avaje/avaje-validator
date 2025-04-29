package io.avaje.validation.generator;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

final class ComponentReader {

  private final ComponentMetaData componentMetaData;
  private final Map<String, ComponentMetaData> privateMetaData;

  ComponentReader(ComponentMetaData metaData, Map<String, ComponentMetaData> privateMetaData) {
    this.componentMetaData = metaData;
    this.privateMetaData = privateMetaData;
  }

  private static boolean isGeneratedComponent(TypeElement moduleType) {
    return moduleType != null
        && "io.avaje.validation.spi.GeneratedComponent"
            .equals(moduleType.getSuperclass().toString());
  }

  void read() {
    for (String fqn : ProcessingContext.readExistingMetaInfServices()) {
      final TypeElement moduleType = APContext.typeElement(fqn);

      if (isGeneratedComponent(moduleType)) {
        var adapters =
            MetaDataPrism.getInstanceOn(moduleType).value().stream()
                .map(APContext::asTypeElement)
                .toList();

        if (adapters.get(0).getModifiers().contains(Modifier.PUBLIC)) {
          componentMetaData.setFullName(fqn);
          readMetaData(moduleType, componentMetaData);

        } else {
          // non-public adapters grouped by packageName
          var packageName =
              APContext.elements().getPackageOf(moduleType).getQualifiedName().toString();
          var meta = privateMetaData.computeIfAbsent(packageName, k -> new ComponentMetaData());
          readMetaData(moduleType, meta);
        }
      }
    }
  }

  /**
   * Read the existing adapters from the MetaData annotation of the generated component.
   *
   * @param meta
   */
  private void readMetaData(TypeElement moduleType, ComponentMetaData meta) {
    for (final AnnotationMirror annotationMirror : moduleType.getAnnotationMirrors()) {
      final MetaDataPrism metaData = MetaDataPrism.getInstance(annotationMirror);
      final ValidFactoryPrism metaDataFactory = ValidFactoryPrism.getInstance(annotationMirror);
      final AnnotationFactoryPrism metaDataAnnotationFactory =
          AnnotationFactoryPrism.getInstance(annotationMirror);

      if (metaData != null) {
        metaData.value().stream().map(TypeMirror::toString).forEach(meta::add);
      }
      if (metaDataFactory != null) {
        metaDataFactory.value().stream().map(TypeMirror::toString).forEach(meta::add);
      }
      if (metaDataAnnotationFactory != null) {
        metaDataAnnotationFactory.value().stream()
            .map(APContext::asTypeElement)
            .forEach(meta::addAnnotationAdapter);
      }
    }
  }
}
