package io.avaje.validation.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.TypeElement;

final class ComponentMetaData {

  private final List<String> allTypes = new ArrayList<>();
  private final List<String> factoryTypes = new ArrayList<>();
  private final List<TypeElement> annotationAdapters = new ArrayList<>();
  private String fullName;

  @Override
  public String toString() {
    return allTypes.toString();
  }

  /** Ensure the component name has been initialised. */
  void initialiseFullName() {
    fullName();
  }

  boolean contains(String type) {
    return allTypes.contains(type);
  }

  void add(String type) {
    allTypes.add(type);
  }

  public void addAnnotationAdapter(TypeElement typeElement) {
    annotationAdapters.add(typeElement);
  }

  void addFactory(String fullName) {
    factoryTypes.add(fullName);
  }

  void setFullName(String fullName) {
    this.fullName = fullName;
  }

  String fullName() {
    if (fullName == null) {
      final List<String> types = new ArrayList<>(allTypes);

      for (final var adapter : annotationAdapters)
        adapter.getQualifiedName().toString().transform(types::add);

      String topPackage = TopPackage.of(types);
      if (!topPackage.endsWith(".valid")) {
        topPackage += ".valid";
      }
      fullName = topPackage + ".GeneratedValidatorComponent";
    }
    return fullName;
  }

  String packageName() {
    return Util.packageOf(fullName());
  }

  List<String> all() {
    return allTypes;
  }

  List<String> allFactories() {
    return factoryTypes;
  }

  List<TypeElement> allAnnotationAdapters() {
    return annotationAdapters;
  }

  /** Return the package imports for the JsonAdapters and related types. */
  Collection<String> allImports() {
    final Set<String> packageImports = new TreeSet<>();
    for (final String adapterFullName : allTypes) {
      packageImports.add(Util.packageOf(adapterFullName) + ".*");
      packageImports.add(Util.baseTypeOfAdapter(adapterFullName));
    }

    for (final var adapter : annotationAdapters) {
      final var adapterFullName = adapter.getQualifiedName().toString();
      packageImports.add(Util.packageOf(adapterFullName) + ".*");
      packageImports.add(Util.baseTypeOfAdapter(adapterFullName));

      AnnotationValidatorPrism.getInstanceOn(adapter)
          .value()
          .toString()
          .transform(packageImports::add);
    }

    return packageImports;
  }
}
