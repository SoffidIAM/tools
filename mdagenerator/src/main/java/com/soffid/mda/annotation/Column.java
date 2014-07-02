package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	String translated() default "";
	String name ();
	int length() default 0;
	String defaultValue () default ""; 
	String reverseAttribute() default "";
	boolean cascadeDelete() default false;
	boolean cascadeNullify() default false;
	boolean composition() default false;
}
