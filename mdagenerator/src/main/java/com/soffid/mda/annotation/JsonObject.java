package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonObject {
	boolean serializerDelegate() default false;
	Class<?> hibernateClass() ;
	String createdOnAttribute() default "createdOn";
	String createdByAttribute() default "createdBy";
	String updatedOnAttribute() default "updatedOn";
	String updatedByAttribute() default "updatedBy";
	String deletedOnAttribute() default "deletedOn";
	String deletedByAttribute() default "deletedBy";
	String startAttribute() default "createdOn";
	String endAttribute() default "";
}
