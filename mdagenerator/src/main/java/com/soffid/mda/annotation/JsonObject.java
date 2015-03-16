package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonObject {
	String servicePath() default "";
	Class serviceClass();
	String createMethod() default "create";
	String updateMethod() default "update";
	String deleteMethod() default "delete";
	String serializeDelegate() default "";
	String serviceDelegate() default "";
	
}
