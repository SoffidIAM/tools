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
import com.soffid.mda.generator.Generator;
import com.soffid.mda.generator.Translate;
import com.soffid.mda.generator.Util;

public class ModelOperation extends ModelElement {
	private Method method;
	LinkedList<ModelParameter> params = null;
	private ModelParameter returnParameter;
	private AbstractModelClass modelClass;
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
		return getName(Translate.DEFAULT);
	}

	public String getPrettySpec(int scope) {
		return getSpec(false, true, scope);
	}

	public String getSpec(int scope) {
		return getSpec(false, false, scope);
	}

	private String getSpec(boolean full, boolean pretty, int scope) {
		StringBuffer b = new StringBuffer ();
		if (getReturnParameter().getDataType().isEntity())
			b.append(getReturnType());
		else
			b.append(getReturnType(scope));
		b.append (" ");
		if (full)
		{
			b.append (getModelClass().getFullName())
				.append (".");
		}
		b.append (getName(scope));
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
			
			AbstractModelClass dt = param.getDataType();
			if (dt.isEntity())
				b.append (dt.getJavaType());
			else
				b.append (dt.getJavaType(scope));
			b.append (" ")
				.append (param.getName());
		}
		b.append (")");
		return b.toString();
	}

	public String getArguments(int scope) {
		StringBuffer b = new StringBuffer ();
		boolean first = true;
		for (ModelParameter param: getParameters())
		{
			if (first)
				first = false;
			else
				b.append (", ");
			b.append (param.getDataType().getJavaType(scope));
		}
		return b.toString();
	}
	
	public String getName(int scope) {
		if (!Translate.mustTranslate(getModelClass(), scope))
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

	public AbstractModelClass getModelClass ()
	{
		if (modelClass == null)
		{
			modelClass = (AbstractModelClass) parser.getElement(method.getDeclaringClass());
		}
		return modelClass;
	}
	public String getReturnType ()
	{
		return getReturnParameter().getDataType().getJavaType();
	}

	public String getReturnType (int scope)
	{
		return getReturnParameter().getDataType().getJavaType(scope);
	}
	
	public String getThrowsClause (int scope)
	{
		StringBuffer clause = new StringBuffer();
		if (getModelClass().isService() || (getModelClass().isEntity() && ! isQuery() && isStatic()))
			clause.append ( 
				"throws "+parser.getDefaultException());
		for (Class th: method.getExceptionTypes())
		{
			if (clause.length() == 0)
				clause.append( "throws ");
			else
				clause.append (", ");
			AbstractModelClass m = (AbstractModelClass) parser.getElement(th);
			clause.append (m.getFullName(scope));
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

	public String getFullSpec(int  scope) {
		return getSpec(true, false, scope);
	}

	public String getImplCall(int scope) {
		StringBuffer spec = new StringBuffer();
		spec.append( "handle" );
		spec.append (Util.firstUpper(getName(scope))) .append ( "(" );
		boolean first = true;
		for (ModelParameter param: getParameters()) {
			if (first)
				first = false;
			else
				spec.append(", ");
			spec.append ( param.getName(scope));
		}
		spec.append(")");
		return spec.toString();
	}

	public String getImplSpec(int scope) {
		StringBuffer spec = new StringBuffer();
		spec.append (getReturnType(scope));
		spec.append( " handle" );
		spec.append (Util.firstUpper(getName(scope))) .append ( "(" );
		boolean first = true;
		for (ModelParameter param: getParameters()) {
			if (first)
				first = false;
			else
				spec.append(", ");
			spec.append (param.getDataType().getJavaType(scope))
				.append (" ")
			    .append ( param.getName(scope));
		}
		spec.append(")");
		return spec.toString();
	}
	
	public Transactional getTransactional ()
	{
		return method.getAnnotation(Transactional.class);
	}

	Set<AbstractModelClass> actors = null;
	public Set<AbstractModelClass> getActors() {
		if (actors == null)
		{
			actors = new HashSet<AbstractModelClass>();
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
