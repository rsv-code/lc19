package com.lehman.gemini.lc19;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Capsule {
    /**
     * The path defines the resource to handle.
     */
    public String path() default "";
}
