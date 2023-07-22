@GeneratePrism(
    value = io.avaje.validation.constraints.Valid.class,
    name = "AvajeValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(io.avaje.validation.ImportValidPojo.class)
@GeneratePrism(io.avaje.validation.adapter.ConstraintAdapter.class)
@GeneratePrism(
    value = javax.validation.Valid.class,
    name = "JavaxValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(
    value = jakarta.validation.Valid.class,
    name = "JakartaValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(
    value = io.avaje.http.api.Valid.class,
    name = "HttpValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.Factory.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.AnnotationFactory.class)
@GeneratePrism(io.avaje.validation.ValidMethod.class)
@GeneratePrism(io.avaje.inject.Component.class)
@GeneratePrism(io.avaje.validation.spi.BuilderCustomizer.class)
package io.avaje.validation.generator;

import io.avaje.prism.GeneratePrism;
