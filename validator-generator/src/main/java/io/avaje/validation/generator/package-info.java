@GeneratePrism(
    value = io.avaje.validation.Valid.class,
    name = "AvajeValidPrism",
    superInterfaces = ValidPrism.class)
@GeneratePrism(io.avaje.validation.Valid.Import.class)
@GeneratePrism(io.avaje.validation.adapter.AnnotationValidator.class)
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
@GeneratePrism(io.avaje.validation.inject.aspect.ValidateParams.class)
package io.avaje.validation.generator;

import io.avaje.prism.GeneratePrism;
