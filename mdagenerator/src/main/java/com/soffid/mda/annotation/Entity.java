package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity  {
	String translatedPackage() default "";
	String translatedName() default "";
	String table();
	String discriminatorColumn() default "";
	String discriminatorValue () default "";
	String tenantFilter() default "";
}
