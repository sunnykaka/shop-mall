package com.kariqu.buyer.web.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: kyle
 * Date: 13-2-20
 * Time: 上午11:34
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RenderHeaderFooter {
    String value() default "";
}
