package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueObject {
	String translatedName() default "";
	String translatedPackage() default "";
	int cache() default 0;
	int cacheTimeout() default 5000;
	String serialVersion() default "1";
}
