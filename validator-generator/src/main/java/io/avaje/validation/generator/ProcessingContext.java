package io.avaje.validation.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

final class ProcessingContext {

  private static final ThreadLocal<Ctx> CTX = new ThreadLocal<>();

  private static final class Ctx {
    private final ProcessingEnvironment env;
    private final Messager messager;
    private final Filer filer;
    private final Elements elements;
    private final Types types;
    private final int jdkVersion;
    private final String diAnnotation;
    private ModuleElement module;
    private boolean validated;

    Ctx(ProcessingEnvironment env) {
      this.env = env;
      this.messager = env.getMessager();
      this.filer = env.getFiler();
      this.elements = env.getElementUtils();
      this.types = env.getTypeUtils();
      this.jdkVersion = env.getSourceVersion().ordinal();

      final var useComponent = elements.getTypeElement(Constants.COMPONENT) != null;

      final var jakarta = elements.getTypeElement(Constants.SINGLETON_JAKARTA) != null;

      diAnnotation =
          (useComponent
              ? Constants.COMPONENT
              : jakarta ? Constants.SINGLETON_JAKARTA : Constants.SINGLETON_JAVAX);
    }
  }

  private ProcessingContext() {}

  static void init(ProcessingEnvironment processingEnv) {
    CTX.set(new Ctx(processingEnv));
  }

  static int jdkVersion() {
    return CTX.get().jdkVersion;
  }

  static boolean isAssignable2Interface(String type, String superType) {
    return type.equals(superType)
        || Optional.ofNullable(element(type)).stream()
            .flatMap(ProcessingContext::superTypes)
            .anyMatch(superType::equals);
  }

  public static Stream<String> superTypes(Element element) {
    final Types types = CTX.get().types;
    return types.directSupertypes(element.asType()).stream()
        .filter(type -> !type.toString().contains("java.lang.Object"))
        .map(superType -> (TypeElement) types.asElement(superType))
        .flatMap(e -> Stream.concat(superTypes(e), Stream.of(e)))
        .map(Object::toString);
  }

  /** Log an error message. */
  static void logError(Element e, String msg, Object... args) {
    CTX.get().messager.printMessage(Diagnostic.Kind.ERROR, msg.formatted(args), e);
  }

  static void logError(String msg, Object... args) {
    CTX.get().messager.printMessage(Diagnostic.Kind.ERROR, msg.formatted(args));
  }

  static void logWarn(String msg, Object... args) {
    CTX.get().messager.printMessage(Diagnostic.Kind.WARNING, msg.formatted(args));
  }

  static void logDebug(String msg, Object... args) {
    CTX.get().messager.printMessage(Diagnostic.Kind.NOTE, msg.formatted(args));
  }

  /** Create a file writer for the given class name. */
  static JavaFileObject createWriter(String cls) throws IOException {
    return CTX.get().filer.createSourceFile(cls);
  }

  static FileObject createMetaInfWriterFor(String interfaceType) throws IOException {
    return CTX.get().filer.createResource(StandardLocation.CLASS_OUTPUT, "", interfaceType);
  }

  static TypeElement element(String rawType) {
    return CTX.get().elements.getTypeElement(rawType);
  }

  static TypeElement asElement(TypeMirror returnType) {
    return (TypeElement) CTX.get().types.asElement(returnType);
  }

  static ProcessingEnvironment env() {
    return CTX.get().env;
  }

  static String diAnnotation() {
    return CTX.get().diAnnotation;
  }

  static void findModule(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    if (CTX.get().module == null) {
      CTX.get().module =
          annotations.stream()
              .map(roundEnv::getElementsAnnotatedWith)
              .flatMap(Collection::stream)
              .findAny()
              .map(ProcessingContext::getModuleElement)
              .orElse(null);
    }
  }

  static void validateModule(String fqn) {
    var module = CTX.get().module;
    if (module != null && !CTX.get().validated && !module.isUnnamed()) {

      CTX.get().validated = true;
      try {
        var resource =
            CTX.get()
                .filer
                .getResource(StandardLocation.SOURCE_PATH, "", "module-info.java")
                .toUri()
                .toString();
        try (var inputStream = new URI(resource).toURL().openStream();
            var reader = new BufferedReader(new InputStreamReader(inputStream))) {

          var noProvides = reader.lines().noneMatch(s -> s.contains(fqn));

          if (noProvides) {
            logError(
                module,
                "Missing \"provides io.avaje.validation.Validator.GeneratedComponent with %s;\"",
                fqn);
          }
        }
      } catch (Exception e) {
        // can't read module
      }
    }
  }

  static ModuleElement getModuleElement(Element e) {
    if (e == null || e instanceof ModuleElement) {
      return (ModuleElement) e;
    }
    return getModuleElement(e.getEnclosingElement());
  }

  static void clear() {
    CTX.remove();
  }
}
