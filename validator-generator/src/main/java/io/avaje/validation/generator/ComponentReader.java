package io.avaje.validation.generator;

import java.util.List;
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

        if (!hasPkgPrivate(moduleType)) {
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

  private boolean hasPkgPrivate(final TypeElement moduleType) {
    return MetaDataPrism.getOptionalOn(moduleType).map(MetaDataPrism::value).stream()
        .flatMap(List::stream)
        .map(APContext::asTypeElement)
        .findAny()
        .or(
            () ->
                ValidFactoryPrism.getOptionalOn(moduleType).map(ValidFactoryPrism::value).stream()
                    .flatMap(List::stream)
                    .map(APContext::asTypeElement)
                    .findAny())
        .or(
            () ->
                AnnotationFactoryPrism.getOptionalOn(moduleType)
                    .map(AnnotationFactoryPrism::value)
                    .stream()
                    .flatMap(List::stream)
                    .map(APContext::asTypeElement)
                    .findAny())
        .filter(t -> t.getModifiers().contains(Modifier.PUBLIC))
        .isEmpty();
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
