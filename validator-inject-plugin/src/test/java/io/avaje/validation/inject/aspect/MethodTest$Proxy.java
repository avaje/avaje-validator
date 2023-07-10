package io.avaje.validation.inject.aspect;

import java.lang.reflect.Method;
import java.util.List;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.InvocationException;
import io.avaje.inject.aop.MethodInterceptor;

public final class MethodTest$Proxy extends MethodTest {

  private final Method test0;
  private final MethodInterceptor test0ValidateParams;

  public MethodTest$Proxy(AspectProvider<ValidateMethod> validateParams) {
    try {
      test0 = MethodTest.class.getDeclaredMethod("test", List.class, int.class, String.class);
      test0ValidateParams =
          validateParams.interceptor(test0, test0.getAnnotation(ValidateMethod.class));

    } catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void test(List<String> str, int inty, String regular) {
    final var call =
        new Invocation.Run(() -> super.test(str, inty, regular))
            .with(this, test0, str, inty, regular);
    try {
      test0ValidateParams.invoke(call);
    } catch (final RuntimeException ex) {
      ex.addSuppressed(new InvocationException("test proxy threw exception"));
      throw ex;
    } catch (final Throwable t) {
      throw new InvocationException("test proxy threw exception", t);
    }
  }
}
