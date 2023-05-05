@GeneratePrism(io.avaje.validation.ValidPojo.class)
@GeneratePrism(io.avaje.validation.ValidPojo.Import.class)
@GeneratePrism(value = javax.validation.Valid.class, name = "JavaxValidPrism")
@GeneratePrism(value = jakarta.validation.Valid.class, name = "JakartaValidPrism")
@GeneratePrism(value = io.avaje.http.api.Valid.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.Factory.class)
@GeneratePrism(
    value = io.avaje.validation.constraints.Pattern.class,
    name = "AvajePatternPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = jakarta.validation.constraints.Pattern.class,
    name = "JakartaPatternPrism",
    superInterfaces = PatternPrism.class)
@GeneratePrism(
    value = javax.validation.constraints.Pattern.class,
    name = "JavaxPatternPrism",
    superInterfaces = PatternPrism.class)
package io.avaje.validation.generator;

import io.avaje.prism.GeneratePrism;
