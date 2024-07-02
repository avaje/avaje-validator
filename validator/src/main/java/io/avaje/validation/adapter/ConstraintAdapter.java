package io.avaje.validation.adapter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * Marks a type as a Constraint Adapter to be registered automatically.
 *
 * <p> A custom adapter registered using this annotation must have a public constructor accepting a ValidationContext instance, and must extend the AbstractConstraintAdapter/ValidationAdapter class.
 *
 * <h3>Example:</h3>
 *
 * <pre>{@code
 *
 * @ConstraintAdapter(SomeAnnotation.class)
 * public final class CustomAnnotationAdapter extends AbstractConstraintAdapter<Object> {
 *
 *   String value;
 *
 *   public CustomAnnotationAdapter(AdapterCreateRequest req) {
 *      //create a message object for error interpolation and set groups
 *      super(req);
 *
 *      //use the attributes to extract the annotation values
 *      value = (String) req.attribute("value");
 *   }
 *
 *
 * 	 @Override
 *   public boolean isValid(Object value) {
 *
 *     var isValid = ...custom validation based on the attributes;
 *
 *     return isValid;
 *   }
 *
 * }</pre>
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface ConstraintAdapter {

  /** The Annotation this validator targets */
  Class<? extends Annotation> value();
}
