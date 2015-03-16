package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonAttribute {
	String value() default "";
	Class serviceClass();
	String hibernateAttribute() default "";
	String hibernateJoin() default "";
	String deleteMethod() default "delete";
	boolean customSerialize() default false;
}
