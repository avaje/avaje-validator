package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.filer;
import static io.avaje.validation.generator.APContext.getModuleInfoReader;
import static io.avaje.validation.generator.APContext.logError;
import static io.avaje.validation.generator.APContext.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

final class ProcessingContext {

  private static final ThreadLocal<Ctx> CTX = new ThreadLocal<>();

  private static final class Ctx {
    private final String diAnnotation;
    private final boolean warnHttp;
    private final boolean injectPresent;
    private boolean validated;

    Ctx(ProcessingEnvironment env) {
      var elements = env.getElementUtils();

      this.injectPresent = elements.getTypeElement(Constants.COMPONENT) != null;
      this.warnHttp = elements.getTypeElement("io.avaje.http.api.Controller") != null;

      final var jakarta = elements.getTypeElement(Constants.SINGLETON_JAKARTA) != null;

      diAnnotation =
          (injectPresent
              ? Constants.COMPONENT
              : jakarta ? Constants.SINGLETON_JAKARTA : Constants.SINGLETON_JAVAX);
    }
  }

  private ProcessingContext() {}

  static void init(ProcessingEnvironment processingEnv) {
    CTX.set(new Ctx(processingEnv));
    APContext.init(processingEnv);
  }

  static FileObject createMetaInfWriterFor(String interfaceType) throws IOException {
    return filer().createResource(StandardLocation.CLASS_OUTPUT, "", interfaceType);
  }


  static String diAnnotation() {
    return CTX.get().diAnnotation;
  }

  static void validateModule(String fqn) {
    var module = getProjectModuleElement();
    if (module != null && !CTX.get().validated && !module.isUnnamed()) {

      CTX.get().validated = true;
      var injectPresent = CTX.get().injectPresent;
      var warnHttp = CTX.get().warnHttp;

      try (var reader = getModuleInfoReader()) {
        AtomicBoolean noInjectPlugin = new AtomicBoolean(injectPresent);
        AtomicBoolean noHttpPlugin = new AtomicBoolean(warnHttp);
        var noProvides =
            reader
                .lines()
                .map(
                    s -> {
                      if (injectPresent && s.contains("io.avaje.validation.plugin")) {
                        noInjectPlugin.set(false);
                      }

                      if (injectPresent && warnHttp && s.contains("io.avaje.validation.http")) {
                        noInjectPlugin.set(false);
                        noHttpPlugin.set(false);
                      }

                      return s;
                    })
                .noneMatch(s -> s.contains(fqn));

        if (noProvides) {
          logError(
              module,
              "Missing `provides io.avaje.validation.Validator.GeneratedComponent with %s;`",
              fqn);
        }

        if (noHttpPlugin.get()) {
          logWarn(
              module,
              "`requires io.avaje.validation.http` must be explicity added or else avaje-inject may fail to detect the default http validator, validator, and method AOP validator",
              fqn);
        } else if (noInjectPlugin.get()) {
          logWarn(
              module,
              "`requires io.avaje.validation.plugin` must be explicity added or else avaje-inject may fail to detect the default validator and method AOP validator",
              fqn);
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
    APContext.clear();
  }
}
