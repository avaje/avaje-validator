package io.avaje.validation.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor9;

// TODO make this not ugly
public class TypeMirrorVisitor extends AbstractTypeVisitor9<StringBuilder, StringBuilder>
    implements UType {

  private final int depth;

  private final boolean includeAnnotations;

  private final Map<TypeVariable, String> typeVariables;
  private Set<String> allTypes = new HashSet<>();
  private String mainType;
  private String fullType;
  private final List<UType> params = new ArrayList<>();
  private final List<AnnotationMirror> annotations = new ArrayList<>();
  private List<AnnotationMirror> everyAnnotation = new ArrayList<>();

  private String shortType;

  public static TypeMirrorVisitor create(TypeMirror typeMirror) {
    final var v = new TypeMirrorVisitor(1, Map.of());
    final StringBuilder b = new StringBuilder();
    v.fullType = typeMirror.accept(v, b).toString();
    return v;
  }

  private TypeMirrorVisitor() {
    this(1, new HashMap<>());
  }

  private TypeMirrorVisitor(int depth, Map<TypeVariable, String> typeVariables) {
    this.includeAnnotations = true;
    this.depth = depth;
    this.typeVariables = new HashMap<>();
    this.typeVariables.putAll(typeVariables);
  }

  @Override
  public Set<String> importTypes() {
    return allTypes;
  }

  @Override
  public String shortType() {
    if (shortType == null) {
      shortType = shortRawType(fullType, allTypes);
    }
    return shortType;
  }

  @Override
  public String full() {
    return fullType;
  }

  @Override
  public boolean isGeneric() {
    return fullType.contains("<");
  }

  @Override
  public List<UType> genericParams() {
    return params;
  }

  @Override
  public List<AnnotationMirror> annotations() {
    return annotations;
  }

  @Override
  public List<AnnotationMirror> allAnnotationsInType() {
    return everyAnnotation;
  }

  @Override
  public String mainType() {
    return mainType;
  }

  @Override
  public UType param0() {
    return params.isEmpty() ? null : params.get(0);
  }

  @Override
  public UType param1() {
    return params.size() < 2 ? null : params.get(1);
  }

  private static String shortRawType(String rawType, Set<String> allTypes) {
    final Map<String, String> typeMap = new LinkedHashMap<>();
    for (final String val : allTypes) {
      typeMap.put(val, ProcessorUtils.shortType(val));
    }
    String shortRaw = rawType;
    for (final Map.Entry<String, String> entry : typeMap.entrySet()) {
      shortRaw = shortRaw.replace(entry.getKey(), entry.getValue());
    }
    return shortRaw;
  }

  private void child(TypeMirror ct, StringBuilder p, boolean setMain) {

    var child = new TypeMirrorVisitor(depth + 1, typeVariables);
    child.allTypes = allTypes;
    child.everyAnnotation = everyAnnotation;
    var full = ct.accept(child, new StringBuilder()).toString();
    child.fullType = full;
    params.add(child);
    p.append(full);
    if (setMain) {
      mainType = child.mainType;
    }
  }

  private void child(TypeMirror ct, StringBuilder p) {
    child(ct, p, false);
  }

  @Override
  public StringBuilder visitPrimitive(PrimitiveType t, StringBuilder p) {

    if (includeAnnotations) {
      for (final var ta : t.getAnnotationMirrors()) {
        p.append(ta.toString()).append(" ");
        annotations.add(ta);
        everyAnnotation.add(ta);
      }
    }

    var primitiveStr = t.getKind().toString().toLowerCase(Locale.ROOT);
    if (this.mainType == null) {
      mainType = primitiveStr;
    }
    p.append(primitiveStr);

    return p;
  }

  @Override
  public StringBuilder visitNull(NullType t, StringBuilder p) {
    return p;
  }

  @Override
  public StringBuilder visitArray(ArrayType t, StringBuilder p) {

    boolean mainUnset = this.mainType == null;
    final var ct = t.getComponentType();
    child(ct, p, true);
    boolean first = true;
    if (includeAnnotations) {
      for (final var ta : t.getAnnotationMirrors()) {
        if (first) {
          p.append(" ");
          first = false;
        }
        p.append(ta.toString()).append(" ");
        annotations.add(ta);
        everyAnnotation.add(ta);
      }
    }
    p.append("[]");
    if (mainUnset) {
      mainType += "[]";
    }
    return p;
  }

  @Override
  public StringBuilder visitDeclared(DeclaredType t, StringBuilder p) {
    final String fqn = fullyQualfiedName(t, includeAnnotations);
    var trimmed = fullyQualfiedName(t, false);
    if (!fqn.startsWith("java.lang")) {
      allTypes.add(Util.extractTypeWithNest(trimmed));
    }
    if (this.mainType == null) {
      mainType = trimmed;
    }
    p.append(fqn);
    final var tas = t.getTypeArguments();
    if (!tas.isEmpty()) {
      p.append("<");
      boolean first = true;
      for (final var ta : tas) {
        if (!first) {
          p.append(", ");
        }
        child(ta, p);
        first = false;
      }
      p.append(">");
    }
    return p;
  }

  String fullyQualfiedName(DeclaredType t, boolean includeAnnotations) {
    final TypeElement element = (TypeElement) t.asElement();
    final var typeUseAnnotations = t.getAnnotationMirrors();

    if (typeUseAnnotations.isEmpty() || !includeAnnotations) {
      return element.getQualifiedName().toString();
    }
    final StringBuilder sb = new StringBuilder();
    // if not too nested, write annotations before the fqn like @someAnnotation io.YourType
    if (depth < 3) {
      for (final var ta : typeUseAnnotations) {
        sb.append(ta.toString()).append(" ");
      }
    }
    String enclosedPart;
    final Element enclosed = element.getEnclosingElement();
    if (enclosed instanceof final QualifiedNameable qn) {
      enclosedPart = qn.getQualifiedName().toString() + ".";
    } else {
      enclosedPart = "";
    }
    sb.append(enclosedPart);

    // if too nested, write annotations in the fqn like io.@someAnnotation YourType
    if (depth > 2) {
      for (final var ta : typeUseAnnotations) {
        sb.append(ta.toString()).append(" ");
      }
    }
    for (final var ta : typeUseAnnotations) {

      final TypeElement annotation = (TypeElement) ta.getAnnotationType().asElement();
      allTypes.add(annotation.getQualifiedName().toString());
      annotations.add(ta);
      everyAnnotation.add(ta);
    }
    sb.append(element.getSimpleName());
    return sb.toString();
  }

  @Override
  public StringBuilder visitError(ErrorType t, StringBuilder p) {
    return p;
  }

  @Override
  public StringBuilder visitTypeVariable(TypeVariable t, StringBuilder p) {

    /*
     * Types can be recursive so we have to check if we have already done this type.
     */
    final String previous = typeVariables.get(t);

    if (previous != null) {
      p.append(previous);
      return p;
    }
    final StringBuilder sb = new StringBuilder();
    /*
     * We do not have to print the upper and lower bound as those are defined usually
     * on the method.
     */
    if (includeAnnotations) {
      for (final var ta : t.getAnnotationMirrors()) {
        p.append(ta.toString()).append(" ");
        sb.append(ta.toString()).append(" ");
      }
    }
    p.append(t.asElement().getSimpleName().toString());
    sb.append(t.asElement().getSimpleName().toString());
    typeVariables.put(t, sb.toString());

    return p;
  }

  @Override
  public StringBuilder visitWildcard(WildcardType t, StringBuilder p) {
    final var extendsBound = t.getExtendsBound();
    final var superBound = t.getSuperBound();
    for (final var ta : t.getAnnotationMirrors()) {
      p.append(ta.toString()).append(" ");
    }
    if (extendsBound != null) {
      p.append("? extends ");
      child(extendsBound, p);
    } else if (superBound != null) {
      p.append("? super ");
      child(superBound, p);
    } else {
      p.append("?");
    }
    return p;
  }

  @Override
  public StringBuilder visitExecutable(ExecutableType t, StringBuilder p) {
    throw new UnsupportedOperationException("don't support executables");
  }

  @Override
  public StringBuilder visitNoType(NoType t, StringBuilder p) {
    throw new UnsupportedOperationException("don't support NoType");
  }

  @Override
  public StringBuilder visitIntersection(IntersectionType t, StringBuilder p) {
    boolean first = true;
    for (final var b : t.getBounds()) {
      if (first) {
        first = false;
      } else {
        p.append("&");
      }
      child(b, p);
    }
    return p;
  }

  @Override
  public StringBuilder visitUnion(UnionType t, StringBuilder p) {
    throw new UnsupportedOperationException();
  }
}
