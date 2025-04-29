package io.avaje.validation.generator;

import javax.lang.model.element.TypeElement;

final class AdapterName {

  final String shortName;
  final String adapterPackage;
  final String fullName;

  AdapterName(TypeElement type) {
    this(type, false);
  }

  AdapterName(BeanReader beanReader) {
    this(beanReader.beanType(), beanReader.isPkgPrivate());
  }

  AdapterName(TypeElement type, boolean pkgPrivate) {
    String originPackage = APContext.elements().getPackageOf(type).getQualifiedName().toString();
    var name = shortName(type);
    shortName = name.substring(0, name.length() - 1);
    if (pkgPrivate) {
      this.adapterPackage = originPackage;
    } else if ("".equals(originPackage)) {
      this.adapterPackage = "valid";
    } else {
      this.adapterPackage =
          ProcessingContext.isImported(type) ? originPackage + ".valid" : originPackage;
    }
    this.fullName = adapterPackage + "." + shortName + "ValidationAdapter";
  }

  private String shortName(TypeElement origin) {
    var sb = new StringBuilder();
    if (origin.getNestingKind().isNested()) {
      sb.append(shortName((TypeElement) origin.getEnclosingElement()));
    }
    return sb.append(Util.shortName(origin.getSimpleName().toString())).append("$").toString();
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
