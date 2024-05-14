package org.live_server.annotations;

import org.live_server.enumeration.ResponseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GET {
    String path() default "";

    ResponseType produces() default ResponseType.JSON;
}
