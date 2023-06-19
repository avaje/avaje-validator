package io.avaje.validation.adapter;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

/**
 *
 * Marks a type as a Annotation Validator to be registered automatically.
 *
 * <p> A custom adapter registered using this annotation must have a public constructor accepting a ValidationContext instance, and must directly implement the ValidationAdapter Interface.
 *
 * <h3>Example:</h3>
 *
 * <pre>{@code
 * @AnnotationValidator(SomeAnnotation.class)
 * public final class CustomAnnotationAdapter implements ValidationAdapter<Object> {
 *
 *   private final Message message;
 *
 *   public CustomAnnotationAdapter(ValidationContext ctx, Set<Class<?>> groups, Map<String, Object> attributes) {
 *     //create a message object for error interpolation
 *     message = ctx.message("{message.property}");
 *
 *      //use the attributes to extract the annotation values
 *
 *   }
 *
 *
 * 	 @Override
 *   public boolean validate(Object value, ValidationRequest req, String propertyName) {
 *
 *     var isValid = ...custom validation based on the attributes;
 *
 *     if (!isValid) {
 *       req.addViolation(message, propertyName);
 *       return false;
 *     }
 *
 *     return true;
 * }
 *
 * }</pre>
 */
@Target(TYPE)
public @interface AnnotationValidator {

  /** The Annotation this validator targets */
  Class<? extends Annotation> value();
}
