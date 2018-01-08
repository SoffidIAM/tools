package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
	boolean serverOnly () default false;
	boolean consoleOnly () default false;
	boolean simple () default false;
	boolean internal () default false;
	String translatedName () default "";
	String serverPath () default "";
	String serverRole () default "server";
	String translatedPackage() default "";
	Class[] grantees() default {};
	boolean stateful() default false;
	boolean translatedImpl() default false;
}
