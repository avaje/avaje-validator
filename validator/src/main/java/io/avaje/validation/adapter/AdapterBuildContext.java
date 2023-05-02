package io.avaje.validation.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

public interface AdapterBuildContext {

    <T> ValidationAdapter<T> adapter(Class<T> cls);

    <T> ValidationAdapter<T> adapter(Type type);

    <T> ValidationAdapter<T> adapter(Class<? extends Annotation> cls, Map<String, Object> attributes);

    String message(String key, Map<String, Object> attributes);
}
