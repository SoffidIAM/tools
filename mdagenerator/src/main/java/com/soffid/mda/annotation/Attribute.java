package com.soffid.mda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
	String defaultValue () default ""; 
	String translated() default "";
	String entityAttribute() default "";
	String[] synonyms() default {}; 
	String validatorClass() default "";
	String lettercase() default "MIXEDCASE";
	boolean hidden() default false;
	boolean readonly() default false;
	String type() default "";
	String filterExpression() default "";
	String separator() default "";
	boolean multivalue() default false;
	boolean multiline() default false;
	boolean searchCriteria() default false;
	String customUiHandler() default "";
	String[] listOfValues() default {};
}
