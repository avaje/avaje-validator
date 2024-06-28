package io.avaje.validation.generator;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

final class AdapterName {

  final String shortName;
  final String adapterPackage;
  final String fullName;

  AdapterName(TypeElement origin) {
    String originName = origin.getQualifiedName().toString();
    String originPackage = ProcessorUtils.packageOf(originName);
    var utype = UType.parse(origin.asType());
    var sb =
        new StringBuilder(
            ProcessorUtils.shortType(utype.mainType()).replace(".", "$").replace("[]", "Array"));
    utype.componentTypes().stream()
        .filter(u -> u.kind() != TypeKind.TYPEVAR && u.kind() != TypeKind.WILDCARD)
        .forEach(u -> sb.append(utype.shortType().replace(".", "$").replace("[]", "Array")));

    shortName = sb.toString();
    if ("".equals(originPackage)) {
      this.adapterPackage = "valid";
    } else {
      this.adapterPackage = ProcessingContext.isImported(origin) ? originPackage + ".valid" : originPackage;
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
