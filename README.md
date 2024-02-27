# [Avaje Validator](https://avaje.io/validator/)

[![Discord](https://img.shields.io/discord/1074074312421683250?color=%237289da&label=discord)](https://discord.gg/Qcqf9R27BR)
[![Build](https://github.com/avaje/avaje-validator/actions/workflows/build.yml/badge.svg)](https://github.com/avaje/avaje-validator/actions/workflows/build.yml)
[![native image build](https://github.com/avaje/avaje-validator/actions/workflows/native-image.yml/badge.svg)](https://github.com/avaje/avaje-validator/actions/workflows/native-image.yml)
[![Maven Central : avaje-validator](https://img.shields.io/maven-central/v/io.avaje/avaje-validator.svg?label=Maven%20Central)](https://maven-badges.herokuapp.com/maven-central/io.avaje/avaje-validator)
[![javadoc](https://javadoc.io/badge2/io.avaje/avaje-validator/javadoc.svg?color=purple)](https://javadoc.io/doc/io.avaje/avaje-validator/latest/io.avaje-validator/io/avaje/validator/package-summary.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/avaje/avaje-inject/blob/master/LICENSE)
Reflection-free pojo validation via apt source code generation. A light (~120kb + generated code) source code generation style alternative to Hibernate Validation. (code generation vs reflection)

- Annotate java classes with `@Valid` (or use `@ImportValidPojo` for types we "don't own" such as external dependencies)
- `avaje-validator-generator` annotation processor generates Java source code to write validation classes
- Supports Avaje/Jakarta/Javax Constraint Annotations
- Groups Support
- Class level Constraints
- Composable Contraint Annotations
- Inheritable Constraints
- loading and interpolating error messages (with multiple Locales) through ResourceBundles
- Getter Validation
- Method parameter validation (requires a DI container to retrieve the generated MethodAdapter classes)

# Quick Start

## Step 1 - Add dependencies
```xml
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-validator</artifactId>
  <version>${avaje.validator.version}</version>
</dependency>
<!-- Alternatively can use Jakarta/Javax Constraints-->
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-validator-constraints</artifactId>
  <version>${avaje.validator.version}</version>
</dependency>
```

And add avaje-validator-generator as an annotation processor.
```xml

<!-- Annotation processors -->
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-validator-generator</artifactId>
  <version>${avaje.validator.version}</version>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

## Step 2 - Add (Avaje/Jakarta/Javax) `@Valid`

Add `@Valid` to the types we want to add validation.

The `avaje-validator-generator` annotation processor will generate validation adapter classes as Java source code
for each type annotated with `@Valid`. These will be automatically registered with `Validator`
when it is started using a service loader mechanism.

```java
@Valid
public class Address {
  @NotBlank
  private String street;

  @NotEmpty(message="must not be empty")
  private List<@NotBlank(message="{message.bundle.key}") String> suburb;

  @Valid
  @NotNull(groups=SomeGroup.class)
  private OtherClass otherclass;

  //add getters/setters
}
```

It also works with records:
```java
@Valid
public record Address(
      @NotBlank String street,
      @NotEmpty(message="must not be empty") String suburb,
      @NotNull(groups=SomeGroup.class) String city
      ) {}
```

For types we cannot annotate with `@Valid` we can place `@ImportValidPojo` on any class/package-info to generate the adapters.

## Step 3 - Use

```java
// build using defaults
Validator validator = Validator.builder().build();

Customer customer = ...;

// will throw a `ConstraintViolationException` containing all the failed constraint violations
validator.validate(customer);

// validate with explicit locale
validator.validate(customer, Locale.ENGLISH);

// validate with groups
validator.validate(customer, Locale.ENGLISH, Group1.class);
```

### Generated Code
Given the class:
```java
@Valid
public record Address(
      @NotBlank String street,
      @NotEmpty(message="must not be empty") String suburb,
      @NotNull(groups=SomeGroup.class) String city
      ) {}
```
The following code will be generated and used for validation.

```java
@Generated
public final class AddressValidationAdapter implements ValidationAdapter<Address> {

  private final ValidationAdapter<String> streetValidationAdapter;
  private final ValidationAdapter<String> suburbValidationAdapter;
  private final ValidationAdapter<String> cityValidationAdapter;

  public AddressValidationAdapter(ValidationContext ctx) {
    this.streetValidationAdapter =
        ctx.<String>adapter(NotBlank.class, Map.of("message","{avaje.NotBlank.message}"));
    this.suburbValidationAdapter =
        ctx.<String>adapter(NotEmpty.class, Map.of("message","must not be empty"));
    this.cityValidationAdapter =
        ctx.<String>adapter(NotNull.class, Map.of("message","{avaje.NotNull.message}", "groups",Set.of(example.avaje.typeuse.SomeGroup.class)));
  }

  @Override
  public boolean validate(Address value, ValidationRequest request, String propertyName) {
    if (propertyName != null) {
      request.pushPath(propertyName);
    }
    var _$street = value.street();
    streetValidationAdapter.validate(_$street, request, "street");

    var _$suburb = value.suburb();
    suburbValidationAdapter.validate(_$suburb, request, "suburb");

    var _$city = value.city();
    cityValidationAdapter.validate(_$city, request, "city");

    if (propertyName != null) {
      request.popPath();
    }
    return true;
  }
}
```
