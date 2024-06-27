package io.avaje.validation.generator;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

final class MethodReader {

  private final ExecutableElement element;
  private final String methodName;
  private final List<MethodParam> params = new ArrayList<>();

  MethodReader(ExecutableElement element, TypeElement beanType) {
    this.element = element;
    this.methodName = element.getSimpleName().toString();
  }

  @Override
  public String toString() {
    return methodName;
  }

  MethodReader read() {
    final List<? extends VariableElement> ps = element.getParameters();
    for (final VariableElement p : ps) {
      params.add(new MethodParam(p));
    }
    return this;
  }

  String getName() {
    return methodName;
  }

  List<MethodParam> getParams() {
    return params;
  }

  public boolean isPublic() {
    return Util.isPublic(element);
  }

  public boolean isProtected() {
    return element.getModifiers().contains(Modifier.PROTECTED);
  }

  static class MethodParam {

    private final String simpleName;

    MethodParam(VariableElement param) {
      this.simpleName = param.getSimpleName().toString();
    }

    String name() {
      return simpleName;
    }
  }
}
