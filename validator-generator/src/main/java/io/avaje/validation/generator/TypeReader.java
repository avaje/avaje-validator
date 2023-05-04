package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.asElement;
import static io.avaje.validation.generator.ProcessingContext.logDebug;
import static io.avaje.validation.generator.ProcessingContext.logError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Read points for field injection and method injection on baseType plus inherited injection points.
 */
final class TypeReader {

  private static final String JAVA_LANG_OBJECT = "java.lang.Object";

  private final List<FieldReader> allFields = new ArrayList<>();
  private final Map<String, FieldReader> allFieldMap = new HashMap<>();
  private final Map<String, MethodReader> allGetterMethods = new LinkedHashMap<>();
  private final Map<String, MethodReader> maybeGetterMethods = new LinkedHashMap<>();
  private final TypeElement baseType;
  private final boolean hasJsonAnnotation;
  private boolean nonAccessibleField;
  private final List<String> genericTypeParams;

  TypeReader(TypeElement baseType) {
    this.baseType = baseType;
    this.hasJsonAnnotation = Util.isValid(baseType);
    this.genericTypeParams = initTypeParams(baseType);
  }

  void read(TypeElement type) {
    final List<FieldReader> localFields = new ArrayList<>();
    for (final Element element : type.getEnclosedElements()) {
      switch (element.getKind()) {
        case FIELD:
          readField(element, localFields);
          break;
        case METHOD:
          readMethod(element, type, localFields);
          break;
        default:
          break;
      }
    }

    for (final FieldReader localField : localFields) {

      allFields.add(localField);
      allFieldMap.put(localField.fieldName(), localField);
    }
  }

  private void readField(Element element, List<FieldReader> localFields) {

    if (includeField(element)) {
      localFields.add(new FieldReader(element, genericTypeParams));
    }
  }

  private List<String> initTypeParams(TypeElement beanType) {
    if (beanType.getTypeParameters().isEmpty()) {
      return Collections.emptyList();
    }
    return beanType.getTypeParameters().stream().map(Object::toString).collect(Collectors.toList());
  }

  int genericTypeParamsCount() {
    return genericTypeParams.size();
  }

  private boolean includeField(Element element) {
    return !element.getModifiers().contains(Modifier.TRANSIENT)
        && !element.getAnnotationMirrors().isEmpty();
  }

  private void readMethod(Element element, TypeElement type, List<FieldReader> localFields) {

    final ExecutableElement methodElement = (ExecutableElement) element;
    if (methodElement.getModifiers().contains(Modifier.PUBLIC)) {
      final List<? extends VariableElement> parameters = methodElement.getParameters();
      final String methodKey = methodElement.getSimpleName().toString();
      final MethodReader methodReader = new MethodReader(methodElement, type).read();
      if (parameters.size() == 0) {
        if (!maybeGetterMethods.containsKey(methodKey)) {
          maybeGetterMethods.put(methodKey, methodReader);
        }
        allGetterMethods.put(methodKey.toLowerCase(), methodReader);
      }
      // for reading methods
      if (includeField(element) && methodElement.getParameters().isEmpty()) {
        final var reader = new FieldReader(element, genericTypeParams);
        localFields.add(reader);
        reader.getterMethod(new MethodReader(methodElement, type));
      }
    }
  }

  private void matchFieldsToGetter() {
    for (final FieldReader field : allFields) {
      matchFieldToGetter(field);
    }
  }

  private void matchFieldToGetter(FieldReader field) {
    if (!matchFieldToGetter2(field, false)
        && !matchFieldToGetter2(field, true)
        && !field.isPublicField()) {
      nonAccessibleField = true;
      if (hasJsonAnnotation) {
        logError(
            "Non accessible field "
                + baseType
                + " "
                + field.fieldName()
                + " with no matching getter?");
      } else {
        logDebug("Non accessible field " + baseType + " " + field.fieldName());
      }
    }
  }

  private boolean matchFieldToGetter2(FieldReader field, boolean loose) {
    final String name = field.fieldName();
    MethodReader getter = getterLookup(name, loose);
    if (getter != null) {
      field.getterMethod(getter);
      return true;
    }
    getter = getterLookup(getterName(name), loose);
    if (getter != null) {
      field.getterMethod(getter);
      return true;
    }
    getter = getterLookup(isGetterName(name), loose);
    if (getter != null) {
      field.getterMethod(getter);
      return true;
    }
    if (field.typeObjectBooleanWithIsPrefix()) { // isRegistered -> getRegistered() for Boolean
      getter = getterLookup(getterName(name.substring(2)), loose);
      if (getter != null) {
        field.getterMethod(getter);
        return true;
      }
    }
    return false;
  }

  private MethodReader getterLookup(String name, boolean loose) {
    if (!loose) {
      return maybeGetterMethods.get(name);
    } else {
      return allGetterMethods.get(name.toLowerCase());
    }
  }

  private String getterName(String name) {
    return "get" + Util.initCap(name);
  }

  private String isGetterName(String name) {
    return "is" + Util.initCap(name);
  }

  boolean nonAccessibleField() {
    return nonAccessibleField;
  }

  List<FieldReader> allFields() {
    return allFields;
  }

  void process() {
    final String base = baseType.getQualifiedName().toString();
    if (!GenericType.isGeneric(base)) {
      read(baseType);
    }
    final TypeElement superElement = superOf(baseType);
    if (superElement != null) {
      addSuperType(superElement);
    }
    processCompleted();
  }

  void processCompleted() {
    matchFieldsToGetter();
  }

  private void addSuperType(TypeElement element) {
    final String type = element.getQualifiedName().toString();
    if (!JAVA_LANG_OBJECT.equals(type) && !GenericType.isGeneric(type)) {
      read(element);
      addSuperType(superOf(element));
    }
  }

  private TypeElement superOf(TypeElement element) {
    return (TypeElement) asElement(element.getSuperclass());
  }
}
