package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.filer;
import static io.avaje.validation.generator.APContext.getModuleInfoReader;
import static io.avaje.validation.generator.APContext.getProjectModuleElement;
import static io.avaje.validation.generator.APContext.logWarn;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.avaje.validation.generator.ModuleInfoReader.Requires;

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
    return filer()
        .createResource(
            StandardLocation.CLASS_OUTPUT,
            "",
            interfaceType.replace("META-INF/services/", "META-INF/generated-services/"));
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
        var moduleInfo = new ModuleInfoReader(module, reader);
        var buildPluginAvailable = buildPluginAvailable();
        var requireSet =
            moduleInfo.requires().stream()
                .map(Requires::getDependency)
                .map(m -> m.getQualifiedName().toString())
                .collect(toSet());

        boolean noHttpPlugin =
            injectPresent
                && (!buildPluginAvailable || !requireSet.contains("io.avaje.http.api"))
                && warnHttp
                && !moduleInfo.containsOnModulePath("io.avaje.validation.http");

        boolean noInjectPlugin =
            noHttpPlugin
                && injectPresent
                && (!buildPluginAvailable || !requireSet.contains("io.avaje.validation"))
                && !moduleInfo.containsOnModulePath("io.avaje.validation.plugin");

        if (noHttpPlugin) {
          logWarn(
              module,
              "`requires io.avaje.validation.http` must be explicity added or else avaje-inject may fail to detect the default http validator, validator, and method AOP validator",
              fqn);
        } else if (noInjectPlugin) {
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

  private static boolean buildPluginAvailable() {

    return resource("target/avaje-plugin-exists.txt", "/target/classes")
        || resource("build/avaje-plugin-exists.txt", "/build/classes/java/main");
  }

  private static boolean resource(String relativeName, String replace) {
    try (var inputStream =
        new URI(
                filer()
                    .getResource(StandardLocation.CLASS_OUTPUT, "", relativeName)
                    .toUri()
                    .toString()
                    .replace(replace, ""))
            .toURL()
            .openStream()) {

      return inputStream.available() > 0;
    } catch (IOException | URISyntaxException e) {
      return false;
    }
  }

  static void clear() {
    CTX.remove();
    APContext.clear();
  }
}
