package org.jdbi.v3.sqlobject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class DefaultFallbackBinderFactory implements FallbackBinderFactory<Bind> {
    @Override public Binder<Bind, Object> fallbackFor(Method method, int param_idx) {
        return new DefaultObjectBinder(param_idx);
    }
}
