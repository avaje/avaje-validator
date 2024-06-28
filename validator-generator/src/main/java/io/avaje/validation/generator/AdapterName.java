package io.avaje.validation.generator;

import javax.lang.model.element.TypeElement;

final class AdapterName {

  final String shortName;
  final String adapterPackage;
  final String fullName;

  AdapterName(TypeElement origin) {
    String originPackage = APContext.elements().getPackageOf(origin).toString().toString();
    shortName = UType.parse(origin.asType()).shortWithoutAnnotations().replace(".", "$");
    if ("".equals(originPackage)) {
      this.adapterPackage = "valid";
    } else {
      this.adapterPackage =
          ProcessingContext.isImported(origin) ? originPackage + ".valid" : originPackage;
    }
    this.fullName = adapterPackage + "." + shortName + "ValidationAdapter";
  }

  String shortName() {
    return shortName;
  }

  String adapterPackage() {
    return adapterPackage;
  }

  String fullName() {
    return fullName;
  }
}
