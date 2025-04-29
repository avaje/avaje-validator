package io.avaje.validation.generator;

import static io.avaje.validation.generator.APContext.asTypeElement;
import static io.avaje.validation.generator.APContext.logError;
import static io.avaje.validation.generator.APContext.typeElement;
import static io.avaje.validation.generator.ProcessingContext.createMetaInfWriterFor;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GenerateModuleInfoReader;
import io.avaje.prism.GenerateUtils;

@GenerateUtils
@GenerateAPContext
@GenerateModuleInfoReader
@SupportedAnnotationTypes({
  AvajeValidPrism.PRISM_TYPE,
  ImportValidPojoPrism.PRISM_TYPE,
  HttpValidPrism.PRISM_TYPE,
  JavaxValidPrism.PRISM_TYPE,
  JakartaValidPrism.PRISM_TYPE,
  ConstraintAdapterPrism.PRISM_TYPE,
  AvajeConstraintPrism.PRISM_TYPE,
  JakartaConstraintPrism.PRISM_TYPE,
  JavaxConstraintPrism.PRISM_TYPE,
  CrossParamConstraintPrism.PRISM_TYPE,
  ValidMethodPrism.PRISM_TYPE,
  ValidSubTypesPrism.PRISM_TYPE,
  "io.avaje.spi.ServiceProvider"
})
public final class ValidationProcessor extends AbstractProcessor {

  private final ComponentMetaData metaData = new ComponentMetaData();
  private final Map<String, ComponentMetaData> privateMetaData = new HashMap<>();
  private final List<BeanReader> allReaders = new ArrayList<>();
  private final Set<String> sourceTypes = new HashSet<>();
  private final Set<String> alreadyGenerated = new HashSet<>();
  private final Set<String> mixInImports = new HashSet<>();
  private final SimpleComponentWriter componentWriter = new SimpleComponentWriter(metaData);
  private boolean readModuleInfo;
  private boolean processedAnything;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    ProcessingContext.init(processingEnv);

    try {

      var file = APContext.getBuildResource("avaje-processors.txt");
      var addition = new StringBuilder();
      if (file.toFile().exists()) {
        var result =
            Stream.concat(Files.lines(file), Stream.of("avaje-validator-generator"))
                .distinct()
                .collect(joining("\n"));
        addition.append(result);
      } else {
        addition.append("avaje-validator-generator");
      }
      Files.writeString(file, addition, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

    } catch (IOException e) {
      // not an issue worth failing over
    }
  }

  /** Read the existing metadata from the generated component (if exists). */
  private void readModule() {
    if (readModuleInfo) {
      return;
    }
    readModuleInfo = true;
    new ComponentReader(metaData, privateMetaData).read();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
    if (round.errorRaised()) {
      return false;
    }
    APContext.setProjectModuleElement(annotations, round);
    readModule();
    getElements(round, AvajeConstraintPrism.PRISM_TYPE).ifPresent(this::writeConstraintAdapters);
    getElements(round, JavaxConstraintPrism.PRISM_TYPE).ifPresent(this::writeConstraintAdapters);
    getElements(round, JakartaConstraintPrism.PRISM_TYPE).ifPresent(this::writeConstraintAdapters);
    getElements(round, CrossParamConstraintPrism.PRISM_TYPE).ifPresent(this::writeConstraintAdapters);

    // register custom adapters
    getElements(round, ConstraintAdapterPrism.PRISM_TYPE).ifPresent(this::registerCustomAdapters);

    getElements(round, AvajeValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, HttpValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, JavaxValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, JakartaValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, ValidMethodPrism.PRISM_TYPE)
        .map(ElementFilter::methodsIn)
        .ifPresent(this::writeParamProviderForMethod);
    getElements(round, MixInPrism.PRISM_TYPE).ifPresent(this::writeAdaptersForMixInTypes);
    getElements(round, ImportValidPojoPrism.PRISM_TYPE).ifPresent(this::writeAdaptersForImported);
    getElements(round, "io.avaje.spi.ServiceProvider").ifPresent(this::registerSPI);
    getElements(round, ValidSubTypesPrism.PRISM_TYPE).ifPresent(this::writeSubTypeAdaptersForImported);

    metaData.fullName(false);
    cascadeTypes();
    writeComponent(round.processingOver());
    return false;
  }

  // Optional because these annotations are not guaranteed to exist
  private Optional<? extends Set<? extends Element>> getElements(RoundEnvironment round, String name) {
    return Optional.ofNullable(typeElement(name)).map(round::getElementsAnnotatedWith);
  }

  private void registerCustomAdapters(Set<? extends Element> elements) {
    for (final var typeElement : ElementFilter.typesIn(elements)) {
      final var type = Util.baseTypeOfAdapter(typeElement);
      final var targetAnnotation = asTypeElement(ConstraintAdapterPrism.getInstanceOn(typeElement).value());
      if (!CrossParamConstraintPrism.getAllOnMetaAnnotations(typeElement).isEmpty() && type.contains("Object[]")) {
        logError(typeElement, "Cross Parameter Adapters must accept type Object[]");
      }

      ConstraintPrism.getOptionalOn(targetAnnotation).ifPresent(p -> {
        if (p.unboxPrimitives() && !Util.isPrimitiveAdapter(typeElement)) {
          if (noPrimitiveValidateMethods(typeElement)) {
            logError(typeElement, "Adapters for Primitive Constraints must override a primitive \"isValid\" or \"validate\" method");
          }
          logError(typeElement, "Adapters for Primitive Constraints must extend PrimitiveAdapter or implement ValidationAdapter.Primitive");
        }
      });

      ElementFilter.constructorsIn(typeElement.getEnclosedElements()).stream()
        .filter(m -> m.getModifiers().contains(Modifier.PUBLIC))
        .filter(m -> m.getParameters().size() == 1)
        .map(m -> m.getParameters().get(0).asType().toString())
        .map(ProcessorUtils::trimAnnotations)
        .filter("io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest"::equals)
        .findAny()
        .ifPresentOrElse(
          x -> {},
          () -> logError(typeElement, "Custom Adapters must have a public constructor with a single AdapterCreateRequest parameter"));

      metaData.addAnnotationAdapter(typeElement);
    }
  }

  private static boolean noPrimitiveValidateMethods(TypeElement typeElement) {
    return ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
      .filter(m -> "isValid".equals(m.getSimpleName().toString()) || "validate".equals(m.getSimpleName().toString()))
      .toList()
      .size() < 2;
  }

  private void cascadeTypes() {
    while (!allReaders.isEmpty()) {
      cascadeTypesInner();
    }
  }

  private void cascadeTypesInner() {
    final List<BeanReader> copy = new ArrayList<>(allReaders);
    allReaders.clear();

    final Set<String> extraTypes = new TreeSet<>();
    for (final BeanReader reader : copy) {
      reader.cascadeTypes(extraTypes);
    }
    for (final String type : extraTypes) {
      if (!ignoreType(type)) {
        final TypeElement element = typeElement(type);
        if (cascadeElement(element)) {
          writeAdapterForType(element);
        }
      }
    }
  }

  private boolean cascadeElement(TypeElement element) {
    return element != null
        && element.getKind() != ElementKind.ENUM
        && !alreadyGenerated.contains(element.toString());
  }

  private boolean ignoreType(String type) {
    return type.indexOf('.') == -1
        || type.startsWith("java.")
        || type.startsWith("javax.")
        || sourceTypes.contains(type);
  }

  /** Elements that have a {@code @ValidSubTypes} annotation. */
  private void writeSubTypeAdaptersForImported(Set<? extends Element> subtypeElements) {
    for (final var element : ElementFilter.typesIn(subtypeElements)) {
      var prism = ValidSubTypesPrism.getInstanceOn(element);
      var subtypes = new ArrayList<>(prism.value());
      subtypes.addAll(element.getPermittedSubclasses());

      var seen = new HashSet<>();
      subtypes.removeIf(s -> !seen.add(s.toString()));
      var writer = new SubTypeWriter(element, subtypes);
      if (!alreadyGenerated.add(element.getQualifiedName().toString())) {
        continue;
      }
      writer.write();
      metaData.add(writer.fullName());
      // cascade types
      for (final TypeMirror importType : subtypes) {
        // if imported by mixin annotation skip
        if (mixInImports.contains(importType.toString())) {
          continue;
        }
        writeAdapterForType(asTypeElement(importType));
      }
    }
  }

  /** Elements that have a {@code @Valid.Import} annotation. */
  private void writeAdaptersForImported(Set<? extends Element> importedElements) {
    for (final var importedElement : ElementFilter.typesIn(importedElements)) {
      for (final TypeMirror importType : ImportValidPojoPrism.getInstanceOn(importedElement).value()) {
        // if imported by mixin annotation skip
        if (mixInImports.contains(importType.toString())) {
          continue;
        }
        writeAdapterForType(asTypeElement(importType));
      }
    }
  }

  /** Elements that have a {@code @MixIn} annotation. */
  private void writeAdaptersForMixInTypes(Set<? extends Element> mixInElements) {
    for (final Element mixin : mixInElements) {
      final TypeMirror mirror = MixInPrism.getInstanceOn(mixin).value();
      final String importType = mirror.toString();
      final TypeElement element = asTypeElement(mirror);
      mixInImports.add(importType);
      writeAdapterForMixInType(element, asTypeElement(mixin.asType()));
    }
  }

  private void writeComponent(boolean processingOver) {
    if (processingOver && processedAnything) {
      try {
        if (!metaData.all().isEmpty()) {
          componentWriter.initialise(false);
          componentWriter.write();
        }

        for (var meta : privateMetaData.values()) {
          if (meta.all().isEmpty()) {
            continue;
          }
          var writer = new SimpleComponentWriter(meta);
          writer.initialise(true);
          writer.write();
        }
        writeMetaInf();
        ProcessingContext.validateModule();
      } catch (final IOException e) {
        logError("Error writing component", e);
      } finally {
        ProcessingContext.clear();
      }
    }
  }

  /** Read the beans that have changed. */
  private void writeAdapters(Set<? extends Element> beans) {
    ElementFilter.typesIn(beans).forEach(this::writeAdapterForType);
  }

  private void writeAdapterForMixInType(TypeElement typeElement, TypeElement mixin) {
    final ClassReader beanReader = new ClassReader(typeElement, mixin);
    writeAdapter(typeElement, beanReader);
  }

  /** Read the beans that have changed. */
  private void writeConstraintAdapters(Set<? extends Element> beans) {
    ElementFilter.typesIn(beans).stream()
      .filter(type -> type.getAnnotationMirrors().stream()
        .anyMatch(m -> ConstraintPrism.isPresent(m.getAnnotationType().asElement())))
      .forEach(this::writeAdapterForConstraint);
  }

  private void writeAdapterForType(TypeElement typeElement) {
    writeAdapter(typeElement, new ClassReader(typeElement));
  }

  private boolean isController(TypeElement typeElement) {
    return typeElement.getAnnotationMirrors().stream()
      .map(AnnotationMirror::getAnnotationType)
      .map(DeclaredType::toString)
      .anyMatch(val -> val.endsWith(".Controller"));
  }

  private void writeAdapterForConstraint(TypeElement typeElement) {
    if (ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
        .noneMatch(m -> "message".equals(m.getSimpleName().toString()))) {
      logError(typeElement, "Constraint annotations must contain a `String message()` method");
    }
    final ContraintReader beanReader = new ContraintReader(typeElement);
    writeAdapter(typeElement, beanReader);
  }

  private void writeAdapter(TypeElement typeElement, BeanReader beanReader) {
    if (isController(typeElement)
        || !alreadyGenerated.add(typeElement.getQualifiedName().toString())) {
      // @Valid on controller just indicating the controller request
      // payloads should be validated - ignore this one
      return;
    }
    processedAnything = true;
    beanReader.read();
    if (beanReader.nonAccessibleField()) {
      if (beanReader.hasValidationAnnotation()) {
        logError("Error ValidationAdapter due to nonAccessibleField for %s ", beanReader);
      }
      return;
    }
    try {
      final SimpleAdapterWriter beanWriter = new SimpleAdapterWriter(beanReader);
      if (beanReader instanceof ClassReader) {
        writeMeta(typeElement, beanReader, beanWriter);
      }
      beanWriter.write();
      allReaders.add(beanReader);
      sourceTypes.add(typeElement.getSimpleName().toString());
    } catch (final IOException e) {
      logError("Error writing ValidationAdapter for %s %s", beanReader, e);
    }
  }

  private void writeMeta(
      TypeElement typeElement, BeanReader beanReader, final SimpleAdapterWriter beanWriter) {
    if (beanReader.isPkgPrivate()) {
      var packageName =
          APContext.elements().getPackageOf(typeElement).getQualifiedName().toString();
      var meta = privateMetaData.computeIfAbsent(packageName, k -> new ComponentMetaData());
      meta.add(beanWriter.fullName());
    } else {
      metaData.add(beanWriter.fullName());
    }
  }

  private void writeParamProviderForMethod(Set<ExecutableElement> elements) {
    for (final ExecutableElement executableElement : elements) {
      if (executableElement.getEnclosingElement().getAnnotationMirrors().stream()
          .map(m -> m.getAnnotationType().toString())
          .noneMatch(ValidationProcessor::isInjectableComponent)) {
        logError(executableElement, "The ValidMethod Annotation can only be used with JSR-330 Injectable Classes");
      }
      writeParamProvider(executableElement);
    }
  }

  private static boolean isInjectableComponent(String annotationType) {
    return annotationType.contains("Singleton")
        || annotationType.contains("Component")
        || annotationType.contains("Service")
        || annotationType.contains("Controller");
  }

  private void writeParamProvider(ExecutableElement typeElement) {
    final ValidMethodReader beanReader = new ValidMethodReader(typeElement);
    try {
      final var beanWriter = new SimpleParamBeanWriter(beanReader);
      if (!alreadyGenerated.add(typeElement.getSimpleName().toString())) {
        return;
      }
      beanWriter.write();
    } catch (final IOException e) {
      logError("Error writing ValidationAdapter for %s %s", beanReader, e);
    }
  }

  private void registerSPI(Set<? extends Element> beans) {
    ElementFilter.typesIn(beans).stream()
      .filter(this::isExtension)
      .map(TypeElement::getQualifiedName)
      .map(Object::toString)
      .forEach(ProcessingContext::addValidatorSpi);
  }

  private boolean isExtension(TypeElement te) {
    return APContext.isAssignable(te, "io.avaje.validation.spi.ValidationExtension");
  }

  private void writeMetaInf() throws IOException {
    var services = ProcessingContext.readExistingMetaInfServices();
    final FileObject fileObject = createMetaInfWriterFor(Constants.META_INF_COMPONENT);
    if (fileObject != null) {
      final Writer writer = fileObject.openWriter();
      writer.write(String.join("\n", services));
      writer.close();
    }
  }
}
