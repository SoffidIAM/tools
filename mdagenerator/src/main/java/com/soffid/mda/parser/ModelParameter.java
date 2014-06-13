package com.soffid.mda.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.Nullable;
import com.soffid.mda.annotation.Param;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

public class ModelParameter extends ModelElement {

	private Type objectClass;
	private LinkedList<ModelAttribute> attributes;
	private Method method;
	private int param;
	private Annotation[] annotations;
	private ModelClass modelClass;
	String name;
	private String translated;

	public ModelParameter(Parser parser, Method method, int param) 
	{
		super (parser);
		this.method = method;
		this.param = param;
		if (param < 0)
		{
			objectClass = method.getGenericReturnType();
			annotations = new Annotation[0];
		} else {
			objectClass = method.getGenericParameterTypes()[param];
			annotations = method.getParameterAnnotations()[param];
			for (int i = 0; i < annotations.length; i++)
			{
				if (annotations[i] instanceof Param)
				{
					name = ((Param) annotations[i]).value();
					translated = ((Param) annotations[i]).translated();
				}
			}
			if (name == null || name.length() == 0)
			{
				String parameterNames [] = new AdaptiveParanamer().lookupParameterNames(method, false);
				if (parameterNames != null && parameterNames.length > param)
					name = parameterNames[param];
				else
					throw new RuntimeException ("Missing attribute name for "+param+" parameter of "+method.toString()+"\nSet method non abstract or add @Param annotation");
			}
		}
		modelClass = (ModelClass) parser.getElement(objectClass) ;
	}

	public ModelClass getDataType() {
		return modelClass;
	}

	@Override
	public String getId() {
		if (param < 0 )
			return method.toString()+": result";
		else
			return method.toString()+": param "+ param;
	}

	@Override
	public String getComments() {
		for (int i = 0; i < annotations.length; i++)
		{
			if (annotations[i] instanceof Description)
			{
				Description description = (Description) annotations[i];
				if (description.value() == null || description.value().length() == 0)
					return null;
				else
					return description.value();
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}
	public String getName(boolean translated) {
		if (translated && this.translated != null && this.translated.length() > 0)
			return this.translated;
		else
			return name;
	}
	
	public boolean isRequired()
	{
		for (int i = 0; i < annotations.length; i++)
		{
			if (annotations[i] instanceof Nullable)
			{
				return false;
			}
		}
		return true;
	}

}
