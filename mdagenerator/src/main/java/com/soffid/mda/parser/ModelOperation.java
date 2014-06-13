package com.soffid.mda.parser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.soffid.mda.annotation.DaoFinder;
import com.soffid.mda.annotation.DaoOperation;
import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.Operation;
import com.soffid.mda.generator.Util;

public class ModelOperation extends ModelElement {
	private Method method;
	LinkedList<ModelParameter> params = null;
	private ModelParameter returnParameter;
	private ModelClass modelClass;
	public ModelOperation (Parser parser, Method m)
	{
		super (parser);
		method = m;
	}

	@Override
	public String getId() {
		return method.toString();
	}

	public List<ModelParameter> getParameters ()
	{
		if (params == null)
		{
			params = new LinkedList<ModelParameter>();
		
			for (int i = 0; i < method.getParameterTypes().length; i++)
			{
				params.add (new ModelParameter(parser, method, i));
			}
		}
		return params;
	}

	public ModelParameter getReturnParameter ()
	{
		if (returnParameter == null)
		{
			returnParameter = new ModelParameter(parser, method, -1);
		}
		return returnParameter;
	}

	@Override
	public String getComments() {
		Description annotation = (Description) method.getAnnotation (Description.class);
		if (annotation != null)
		{
			if (annotation.value() == null || annotation.value().length() == 0)
				return null;
			else
				return annotation.value();
		}
		else
			return null;
	}

	public boolean isStatic() {
		return (method.getModifiers() & Modifier.STATIC) != 0 ||
				method.getAnnotation(DaoOperation.class) != null;
	}
	
	public boolean isQuery ()
	{
		return method.getName().startsWith("find") ||
				method.getAnnotation(DaoFinder.class) != null;
	}

	public String getName() {
		return getName(false);
	}

	public String getPrettySpec(boolean translated) {
		return getSpec(false, true, translated);
	}

	public String getSpec(boolean translated) {
		return getSpec(false, false, translated);
	}

	private String getSpec(boolean full, boolean pretty, boolean translated) {
		StringBuffer b = new StringBuffer ();
		b.append(getReturnType(translated))
			.append (" ");
		if (full)
		{
			b.append (getModelClass().getFullName())
				.append (".");
		}
		b.append (getName(translated));
		b.append("(");
		boolean first = true;
		for (ModelParameter param: getParameters())
		{
			if (first)
				first = false;
			else
				b.append (", ");
			if (pretty)
				b.append ("\n\t\t");
			
			b.append (param.getDataType().getJavaType(translated))
				.append (" ")
				.append (param.getName());
		}
		b.append (")");
		return b.toString();
	}

	public String getArguments(boolean translated) {
		StringBuffer b = new StringBuffer ();
		boolean first = true;
		for (ModelParameter param: getParameters())
		{
			if (first)
				first = false;
			else
				b.append (", ");
			b.append (param.getDataType().getJavaType());
		}
		return b.toString();
	}
	
	public String getName(boolean translated) {
		if (!translated)
			return method.getName();
		else
		{
			Operation opAnnotation = method.getAnnotation(Operation.class) ;
			if (opAnnotation != null && opAnnotation.translated().length() > 0)
			{
				return opAnnotation.translated();
			}
			else
				return method.getName();
		}
	}

	public ModelClass getModelClass ()
	{
		if (modelClass == null)
		{
			modelClass = (ModelClass) parser.getElement(method.getDeclaringClass());
		}
		return modelClass;
	}
	public String getReturnType ()
	{
		return getReturnParameter().getDataType().getJavaType();
	}

	public String getReturnType (boolean translated)
	{
		return getReturnParameter().getDataType().getJavaType(translated);
	}
	
	public String getThrowsClause (boolean translated)
	{
		StringBuffer clause = new StringBuffer();
		if (getModelClass().isService() || (getModelClass().isEntity() && ! isQuery() && isStatic()))
			clause.append ( ! translated ? "throws es.caib.seycon.ng.exception.InternalErrorException":
				"throws com.soffid.iam.exception.InternalErrorException" );
		for (Class th: method.getExceptionTypes())
		{
			if (clause.length() == 0)
				clause.append( "throws ");
			else
				clause.append (", ");
			ModelClass m = (ModelClass) parser.getElement(th);
			clause.append (m.getFullName(translated));
		}


		return clause.toString();
	}
	
	public String getFinderQuery ()
	{
		DaoFinder finder = method.getAnnotation(DaoFinder.class);
		if (finder != null)
		{
			return finder.value();
		}
		else
			return "";
	}

	public List<ModelClass> getExceptions ()
	{
		LinkedList<ModelClass> ex = new LinkedList<ModelClass>();
		for (Class th: method.getExceptionTypes())
		{
			ex.add((ModelClass) parser.getElement(th));
		}


		return ex;
	}

	public String getFullSpec(boolean  translated) {
		return getSpec(true, false, translated);
	}

	public String getImplCall(boolean translated) {
		StringBuffer spec = new StringBuffer();
		spec.append( "handle" );
		spec.append (Util.firstUpper(getName(translated))) .append ( "(" );
		boolean first = true;
		for (ModelParameter param: getParameters()) {
			if (first)
				first = false;
			else
				spec.append(", ");
			spec.append ( param.getName(translated));
		}
		spec.append(")");
		return spec.toString();
	}

	public String getImplSpec(boolean translated) {
		StringBuffer spec = new StringBuffer();
		spec.append (getReturnType());
		spec.append( " handle" );
		spec.append (Util.firstUpper(getName(translated))) .append ( "(" );
		boolean first = true;
		for (ModelParameter param: getParameters()) {
			if (first)
				first = false;
			else
				spec.append(", ");
			spec.append (param.getDataType().getJavaType())
				.append (" ")
			    .append ( param.getName(translated));
		}
		spec.append(")");
		return spec.toString();
	}
	
	public Transactional getTransactional ()
	{
		return method.getAnnotation(Transactional.class);
	}

	Set<ModelClass> actors = null;
	public Set<ModelClass> getActors() {
		if (actors == null)
		{
			actors = new HashSet<ModelClass>();
			Operation op = method.getAnnotation(Operation.class);
			if (op != null)
			{
				for (Class actor: op.grantees())
				{
					actors.add( (ModelClass) parser.getElement(actor));
				}
			}
			actors.addAll(getModelClass().getActors());
		}
		return actors;
	}
}
