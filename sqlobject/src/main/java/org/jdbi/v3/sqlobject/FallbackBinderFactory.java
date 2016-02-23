package org.jdbi.v3.sqlobject;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface FallbackBinderFactory<AnnoType extends Annotation> {
   Binder<AnnoType, Object> fallbackFor(Method method, int param_idx);
}


