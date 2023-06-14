@GeneratePrism(io.avaje.validation.ValidPojo.class)
@GeneratePrism(io.avaje.validation.ValidPojo.Import.class)
@GeneratePrism(io.avaje.validation.adapter.AnnotationValidator.class)
@GeneratePrism(value = javax.validation.Valid.class, name = "JavaxValidPrism")
@GeneratePrism(value = jakarta.validation.Valid.class, name = "JakartaValidPrism")
@GeneratePrism(value = io.avaje.http.api.Valid.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.Factory.class)
@GeneratePrism(io.avaje.validation.spi.MetaData.AnnotationFactory.class)
package io.avaje.validation.generator;

import io.avaje.prism.GeneratePrism;
