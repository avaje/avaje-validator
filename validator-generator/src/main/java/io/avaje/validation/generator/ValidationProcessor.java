package io.avaje.validation.generator;

import static io.avaje.validation.generator.ProcessingContext.asElement;
import static io.avaje.validation.generator.ProcessingContext.element;
import static io.avaje.validation.generator.ProcessingContext.logError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

@SupportedAnnotationTypes({
  AvajeValidPrism.PRISM_TYPE,
  BuilderCustomizerPrism.PRISM_TYPE,
  ImportValidPojoPrism.PRISM_TYPE,
  HttpValidPrism.PRISM_TYPE,
  JavaxValidPrism.PRISM_TYPE,
  JakartaValidPrism.PRISM_TYPE,
  ConstraintAdapterPrism.PRISM_TYPE,
  AvajeConstraintPrism.PRISM_TYPE,
  JakartaConstraintPrism.PRISM_TYPE,
  JavaxConstraintPrism.PRISM_TYPE,
  CrossParamConstraintPrism.PRISM_TYPE,
  ValidMethodPrism.PRISM_TYPE
})
public final class ValidationProcessor extends AbstractProcessor {

  private final ComponentMetaData metaData = new ComponentMetaData();
  private final List<BeanReader> allReaders = new ArrayList<>();
  private final Set<String> sourceTypes = new HashSet<>();
  private final Set<String> mixInImports = new HashSet<>();
  private SimpleComponentWriter componentWriter;
  private boolean readModuleInfo;
  private CustomizerServiceWriter customizerServiceWriter;

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    ProcessingContext.init(processingEnv);
    this.componentWriter = new SimpleComponentWriter(metaData);
    this.customizerServiceWriter = new CustomizerServiceWriter();
  }

  /** Read the existing metadata from the generated component (if exists). */
  private void readModule() {
    if (readModuleInfo) {
      return;
    }
    readModuleInfo = true;
    new ComponentReader(metaData).read();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {

    readModule();

    getElements(round, AvajeConstraintPrism.PRISM_TYPE).ifPresent(this::writeContraintAdapters);
    getElements(round, JavaxConstraintPrism.PRISM_TYPE).ifPresent(this::writeContraintAdapters);
    getElements(round, JakartaConstraintPrism.PRISM_TYPE).ifPresent(this::writeContraintAdapters);
    getElements(round, CrossParamConstraintPrism.PRISM_TYPE).ifPresent(this::writeContraintAdapters);

    //register custom adapters
    getElements(round, ConstraintAdapterPrism.PRISM_TYPE).ifPresent(this::registerCustomAdapters);



    getElements(round, AvajeValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, HttpValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, JavaxValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, JakartaValidPrism.PRISM_TYPE).ifPresent(this::writeAdapters);
    getElements(round, ValidMethodPrism.PRISM_TYPE)
        .map(ElementFilter::methodsIn)
        .ifPresent(this::writeParamProviderForMethod);

    writeAdaptersForImported(
        round.getElementsAnnotatedWith(element(ImportValidPojoPrism.PRISM_TYPE)));
    initialiseComponent();
    cascadeTypes();
    customizerServiceWriter.writeMetaInf(
        round.getElementsAnnotatedWith(element(BuilderCustomizerPrism.PRISM_TYPE)));
    writeComponent(round.processingOver());
    return false;
  }

  // Optional because these annotations are not guaranteed to exist
  private Optional<? extends Set<? extends Element>> getElements(
      RoundEnvironment round, String name) {
    return Optional.ofNullable(element(name)).map(round::getElementsAnnotatedWith);
  }

  private void registerCustomAdapters(Set<? extends Element> elements) {
    for (final var typeElement : ElementFilter.typesIn(elements)) {
      final var type = Util.baseTypeOfAdapter(typeElement);

      if (!CrossParamConstraintPrism.getAllOnMetaAnnotations(typeElement).isEmpty()
          && type.contains("Object[]")) {
        logError(typeElement, "Cross Parameter Adapters must accept type Object[]");
      }

      ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
          .filter(m -> m.getKind() == ElementKind.CONSTRUCTOR)
          .filter(m -> m.getModifiers().contains(Modifier.PUBLIC))
          .filter(m -> m.getParameters().size() == 1)
          .map(m -> m.getParameters().get(0).asType().toString())
          .map(Util::trimAnnotations)
          .filter("io.avaje.validation.adapter.ValidationContext.AdapterCreateRequest"::equals)
          .findAny()
          .ifPresentOrElse(
              x -> {},
              () ->
                  logError(
                      typeElement,
                      "Custom Adapters must have a public contrustor that accepts a single AdapterCreateRequest parameter"));

      metaData.addAnnotationAdapter(typeElement);
    }
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
        final TypeElement element = element(type);
        if (cascadeElement(element)) {
          writeAdapterForType(element);
        }
      }
    }
  }

  private boolean cascadeElement(TypeElement element) {
    return element != null && element.getKind() != ElementKind.ENUM && !metaData.contains(adapterName(element));
  }

  private String adapterName(TypeElement element) {
    return new AdapterName(element).fullName();
  }

  private boolean ignoreType(String type) {
    return type.indexOf('.') == -1
        || type.startsWith("java.")
        || type.startsWith("javax.")
        || sourceTypes.contains(type);
  }

  /** Elements that have a {@code @Valid.Import} annotation. */
  private void writeAdaptersForImported(Set<? extends Element> importedElements) {
    for (final var importedElement : ElementFilter.typesIn(importedElements)) {
      for (final TypeMirror importType :
          ImportValidPojoPrism.getInstanceOn(importedElement).value()) {
        // if imported by mixin annotation skip
        if (mixInImports.contains(importType.toString())) {
          continue;
        }
        writeAdapterForType(asElement(importType));
      }
    }
  }

  private void initialiseComponent() {
    metaData.initialiseFullName();
    try {
      componentWriter.initialise();
    } catch (final IOException e) {
      logError("Error creating writer for ValidationComponent", e);
    }
  }

  private void writeComponent(boolean processingOver) {
    if (processingOver) {
      try {
        customizerServiceWriter.close();
        componentWriter.write();
        componentWriter.writeMetaInf();
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

  /** Read the beans that have changed. */
  private void writeContraintAdapters(Set<? extends Element> beans) {
    ElementFilter.typesIn(beans).stream()
        .filter(
            type ->
                type.getAnnotationMirrors().stream()
                    .anyMatch(m -> ConstraintPrism.isPresent(m.getAnnotationType().asElement())))
        .forEach(this::writeAdapterForConstraint);
  }

  private void writeAdapterForType(TypeElement typeElement) {
    if (isController(typeElement)) {
      // @Valid on controller just indicating the controller request
      // payloads should be validated - ignore this one
      return;
    }
    final ClassReader beanReader = new ClassReader(typeElement);
    writeAdapter(typeElement, beanReader);
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
      logError(typeElement, "Constraint annotations must contain a message method");
    }
    final ContraintReader beanReader = new ContraintReader(typeElement);
    writeAdapter(typeElement, beanReader);
  }

  private void writeAdapter(TypeElement typeElement, BeanReader beanReader) {
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
        metaData.add(beanWriter.fullName());
      }
      beanWriter.write();
      allReaders.add(beanReader);
      sourceTypes.add(typeElement.getSimpleName().toString());
    } catch (final IOException e) {
      logError("Error writing ValidationAdapter for %s %s", beanReader, e);
    }
  }

  private void writeParamProviderForMethod(Set<ExecutableElement> elements) {

    for (final ExecutableElement executableElement : elements) {

      if (executableElement.getEnclosingElement().getAnnotationMirrors().stream()
          .map(m -> m.getAnnotationType().toString())
          .noneMatch(
              s ->
                  s.contains("Singleton")
                      || s.contains("Component")
                      || s.contains("Service")
                      || s.contains("Controller"))) {
        logError(
            executableElement,
            "The ValidMethod Annotation can only be used with JSR-330 Injectable Classes");
      }
      writeParamProvider(executableElement);
    }
  }

  private void writeParamProvider(ExecutableElement typeElement) {
    final ValidMethodReader beanReader = new ValidMethodReader(typeElement);
    try {
      final var beanWriter = new SimpleParamBeanWriter(beanReader);

      beanWriter.write();
    } catch (final IOException e) {
      logError("Error writing ValidationAdapter for %s %s", beanReader, e);
    }
  }
}
