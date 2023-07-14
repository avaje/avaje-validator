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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

@SupportedAnnotationTypes({
  AvajeValidPrism.PRISM_TYPE,
  BuilderCustomizerPrism.PRISM_TYPE,
  ImportValidPojoPrism.PRISM_TYPE,
  HttpValidPrism.PRISM_TYPE,
  JavaxValidPrism.PRISM_TYPE,
  JakartaValidPrism.PRISM_TYPE,
  AnnotationValidatorPrism.PRISM_TYPE,
  AvajeConstraintPrism.PRISM_TYPE,
  JakartaConstraintPrism.PRISM_TYPE,
  JavaxConstraintPrism.PRISM_TYPE,
  ValidateMethodPrism.PRISM_TYPE
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

    // Optional because these annotations are not guaranteed to exist
    Optional.ofNullable(element(AvajeConstraintPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeContraintAdapters);

    Optional.ofNullable(element(JavaxConstraintPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeContraintAdapters);
    Optional.ofNullable(element(JakartaConstraintPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeContraintAdapters);

    registerCustomAdapters(
        round.getElementsAnnotatedWith(element(AnnotationValidatorPrism.PRISM_TYPE)));

    Optional.ofNullable(element(AvajeValidPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeAdapters);
    Optional.ofNullable(element(HttpValidPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeAdapters);
    Optional.ofNullable(element(JavaxValidPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeAdapters);
    Optional.ofNullable(element(JakartaValidPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
        .ifPresent(this::writeAdapters);

    Optional.ofNullable(element(ValidateMethodPrism.PRISM_TYPE))
        .map(round::getElementsAnnotatedWith)
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

  private void registerCustomAdapters(Set<? extends Element> elements) {
    for (final var typeElement : ElementFilter.typesIn(elements)) {
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
    return element.getKind() != ElementKind.ENUM && !metaData.contains(adapterName(element));
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
            t ->
                t.getAnnotationMirrors().stream()
                    .anyMatch(m -> ConstraintPrism.isPresent(m.getAnnotationType().asElement())))
        .forEach(this::writeAdapterForContraint);
  }

  private void writeAdapterForType(TypeElement typeElement) {
    final ClassReader beanReader = new ClassReader(typeElement);
    writeAdapter(typeElement, beanReader);
  }

  private void writeAdapterForContraint(TypeElement typeElement) {

    if (ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
        .noneMatch(m -> "message".equals(m.getSimpleName().toString()))) {
      throw new IllegalStateException("Constraint annotations must contain a message method");
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
          .noneMatch(s -> s.contains("Singleton") || s.contains("Component"))) {
        throw new IllegalStateException(
            "The ValidateMethod Annotation can only be used with JSR-330 Injectable Classes");
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
