package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.filer;
import static io.avaje.validation.generator.APContext.getModuleInfoReader;
import static io.avaje.validation.generator.APContext.getProjectModuleElement;
import static io.avaje.validation.generator.APContext.logWarn;
import static java.util.stream.Collectors.toSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.avaje.validation.generator.ModuleInfoReader.Requires;

final class ProcessingContext {

  private static final ThreadLocal<Ctx> CTX = new ThreadLocal<>();

  private static final class Ctx {
    private final String diAnnotation;
    private final boolean warnHttp;
    private final boolean injectPresent;
    private final Set<String> serviceSet = new TreeSet<>();

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

  static boolean isImported(Element element) {
    var moduleName = APContext.getProjectModuleElement().getQualifiedName();
    return !APContext.elements().getModuleOf(element).getQualifiedName().contentEquals(moduleName);
  }

  static void validateModule() {
    var module = getProjectModuleElement();
    if (module != null && !module.isUnnamed()) {
      var injectPresent = CTX.get().injectPresent;
      var warnHttp = CTX.get().warnHttp;

      try (var reader = getModuleInfoReader()) {
        var moduleInfo = new ModuleInfoReader(module, reader);

        moduleInfo.validateServices("io.avaje.validation.spi.ValidationExtension", CTX.get().serviceSet);

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
          logWarn(module, "`requires io.avaje.validation.http` must be explicity added or else avaje-inject may fail to detect the default http validator, validator, and method AOP validator");
        } else if (noInjectPlugin) {
          logWarn(module, "`requires io.avaje.validation.plugin` must be explicity added or else avaje-inject may fail to detect the default validator and method AOP validator");
        }

      } catch (Exception e) {
        // can't read module
      }
    }
  }

  private static boolean buildPluginAvailable() {
    try {
      return APContext.getBuildResource("avaje-plugin-exists.txt").toFile().exists();
    } catch (final Exception e) {
      return false;
    }
  }

  static Set<String> readExistingMetaInfServices() {
    var services = CTX.get().serviceSet;
    try (final var file =
           APContext.filer()
             .getResource(StandardLocation.CLASS_OUTPUT, "", Constants.META_INF_COMPONENT)
             .toUri()
             .toURL()
             .openStream();
         final var buffer = new BufferedReader(new InputStreamReader(file));) {

      String line;
      while ((line = buffer.readLine()) != null) {
        line.replaceAll("\\s", "").replace(",", "\n").lines().forEach(services::add);
      }
    } catch (Exception e) {
      // not a critical error
    }
    return services;
  }

  static void clear() {
    CTX.remove();
    APContext.clear();
  }

  public static void addValidatorSpi(String spi) {
    CTX.get().serviceSet.add(spi);
  }
}
