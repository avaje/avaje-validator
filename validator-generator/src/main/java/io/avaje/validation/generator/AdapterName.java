package io.avaje.validation.generator;

import javax.lang.model.element.TypeElement;

final class AdapterName {

  final String shortName;
  final String adapterPackage;
  final String fullName;

  AdapterName(TypeElement origin) {
    final String originName = origin.getQualifiedName().toString();
    final String name = origin.getSimpleName().toString();
    String originPackage = ProcessorUtils.packageOf(originName);
    if (origin.getNestingKind().isNested()) {
      final String parent = Util.shortName(originPackage);
      originPackage = ProcessorUtils.packageOf(originPackage);
      shortName = parent + "$" + name;
    } else {
      shortName = name;
    }
    this.adapterPackage = "".equals(originPackage) ? "valid" : originPackage + ".valid";
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
