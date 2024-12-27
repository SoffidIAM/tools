package com.soffid.mda.parser;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.soffid.mda.annotation.ApplicationException;
import com.soffid.mda.annotation.Criteria;
import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.Entity;
import com.soffid.mda.annotation.Enumeration;
import com.soffid.mda.annotation.ForeignKey;
import com.soffid.mda.annotation.Index;
import com.soffid.mda.annotation.JsonObject;
import com.soffid.mda.annotation.Operation;
import com.soffid.mda.annotation.Role;
import com.soffid.mda.annotation.Service;
import com.soffid.mda.annotation.TranslatedClass;
import com.soffid.mda.annotation.ValueObject;
import com.soffid.mda.generator.Generator;
import com.soffid.mda.generator.Translate;
import com.soffid.mda.generator.Util;

public class ModelClass extends AbstractModelClass {

	private Type objectClass;
	private List<ModelOperation> operations;
	private LinkedList<AbstractModelAttribute> attributes;
	private LinkedList<AbstractModelClass> depends;
	private LinkedList<AbstractModelClass> provides = new LinkedList<AbstractModelClass>();
	private LinkedList<AbstractModelClass> specializations = new LinkedList<AbstractModelClass>();

	private LinkedList<AbstractModelAttribute> foreignKeys = new LinkedList<AbstractModelAttribute>();
	
	@Override
	public LinkedList<AbstractModelAttribute> getForeignKeys() {
		return foreignKeys;
	}


	@Override
	public LinkedList<AbstractModelClass> getProvides() {
		return provides;
	}

	@Override
	public LinkedList<AbstractModelClass> getSpecializations() {
		return specializations;
	}

	private Class underlyingClass;
	private boolean _collection;
	ModelClass childCparseObjectClasslass = null;
	private String javaType;

	@Override
	public boolean isGenerated() {
		return generated;
	}

	private boolean generated;
	private boolean _array;
	private Class _collectionClass;
	private boolean _future;
	private ModelClass childClass;
	private ModelClass childrenClasses[];
	
	@Override
	public boolean isArray() {
		return _array;
	}

	public ModelClass(Parser parser, Type classFile) {
		super (parser);
		objectClass = classFile;
		underlyingClass = null;
		_collection = false;
		_future = false;
		
		parseObjectClass(parser);
	}


	public void parseObjectClass(Parser parser) {
		javaType = typeToString (objectClass);
		if (objectClass instanceof Class)
		{
			underlyingClass = (Class) objectClass;
			
			_future = Future.class.isAssignableFrom( (Class) objectClass);
			
			if (underlyingClass.isArray() )
			{
				_array = true;
				childClass =  (ModelClass) parser.getElement(underlyingClass.getComponentType());
			}
		}
		else if (objectClass instanceof ParameterizedType)
		{
			Type rawType = ((ParameterizedType) objectClass).getRawType();
			if (rawType instanceof Class)
			{
				_future = Future.class.isAssignableFrom( (Class) rawType);
				if (Collection.class.isAssignableFrom((Class) rawType))
				{
					_collection = true;
					_collectionClass = (Class) rawType;
				}
				else
					underlyingClass = (Class) rawType;
				Type[] actualTypeArguments = ((ParameterizedType) objectClass).getActualTypeArguments();
				if (actualTypeArguments.length > 0)
				{
					childrenClasses = new ModelClass[actualTypeArguments.length];
					javaType = ((Class) rawType).getCanonicalName();
					for (int i = 0; i < actualTypeArguments.length; i++) {
						Type pt = actualTypeArguments[i];
						childrenClasses[i] = (ModelClass) parser.getElement( pt );
						if (i == 0)
							javaType += "<";
						else
							javaType += ",";
						javaType += childrenClasses[i].getJavaType();
					}
					javaType += ">";
					childClass =  childrenClasses[0];
				}
			}
		}
		else if (objectClass instanceof GenericArrayType)
		{
			_array = true;
			childClass = (ModelClass) parser.getElement(((GenericArrayType) objectClass).getGenericComponentType()); 
		}
	}

	private String typeToString ( Type t )
	{
		if (t instanceof ParameterizedType)
		{
			ParameterizedType pt = (ParameterizedType) t;
			String s = typeToString(((ParameterizedType) t).getRawType()) + "<";
			Type[] params = pt.getActualTypeArguments() ;
			for (int i = 0; i < params.length; i++)
			{
				if (i > 0) s = s + ",";
				s = s + typeToString(params[i]);
			}
			s = s +">";
						
			return s;
		}
		else if  (t instanceof Class){
			Class cl = (Class) t;
			return cl.getCanonicalName();
		} else {
			return t.toString();
		}
	}

	@Override
	public ModelClass getChildClass() {
		return childClass;
	}

	@Override
	public String getId() {
		return objectClass.toString();
	}
	

	protected Annotation getAnnotation (Class cl)
	{
		if (underlyingClass != null)
			return underlyingClass.getAnnotation(cl);
		else
			return null;
	}
	
	@Override
	public boolean isEntity ()
	{
		if (getAnnotation(Entity.class) != null)
			return true;
		else
			return false;
	}


	@Override
	public boolean isService ()
	{
		if (getAnnotation(Service.class) != null)
			return true;
		else
			return false;
	}
	

	@Override
	public boolean isValueObject ()
	{
		if (getAnnotation(ValueObject.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isJsonObject ()
	{
		if (getAnnotation(JsonObject.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isEnumeration ()
	{
		if (getAnnotation(Enumeration.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isRole ()
	{
		if (getAnnotation(Role.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isIndex ()
	{
		if (getAnnotation(Index.class) != null)
			return true;
		else
			return false;
	}
	
	@Override
	public String[] getIndexColumns ()
	{
		Index index = (Index) getAnnotation(Index.class);
		if (index != null)
			return index.columns();
		else
			return null;
	}
	
	@Override
	public AbstractModelClass getIndexEntity ()
	{
		Index index = (Index) getAnnotation(Index.class);
		if (index != null)
		{
			Class cl = index.entity();
			return (AbstractModelClass) parser.getElement(cl);
		}
		else
			return null;
	}
	
	@Override
	public String getIndexName ()
	{
		Index index = (Index) getAnnotation(Index.class);
		if (index != null)
		{
			return index.name();
		}
		else
			return null;
	}

	@Override
	public boolean isIndexUnique ()
	{
		Index index = (Index) getAnnotation(Index.class);
		if (index != null)
		{
			return index.unique();
		}
		else
			return false;
	}

	@Override
	public List<ModelOperation> getOperations ()
	{
		if (operations == null)
		{
			operations = new LinkedList<ModelOperation>();
		
			if (underlyingClass != null)
			{
				for (Method m: underlyingClass.getDeclaredMethods())
				{
					operations.add((ModelOperation) parser.getElement(m));
				}
				Collections.sort(operations, new Comparator<ModelOperation>() {

					public int compare(ModelOperation o1,
							ModelOperation o2) {
						return o1.toString().compareTo(o2.toString());
					}
					
				});
			}
		}
		return operations;
	}

	
	@Override
	public List<AbstractModelAttribute> getAttributes ()
	{
		if (attributes == null)
		{
			attributes = new LinkedList<AbstractModelAttribute>();
		
			if (underlyingClass != null)
			{
				for (Field f: underlyingClass.getDeclaredFields())
				{
					attributes.add((AbstractModelAttribute) parser.getElement(f));
				}
//				sortAttributes();
			}
		}
		return attributes;
	}

	@Override
	public List<AbstractModelClass> getDepends ()
	{
		if (depends == null)
		{
			depends = new LinkedList<AbstractModelClass>();
		
			if (underlyingClass != null)
			{
				Depends annotation = (Depends) underlyingClass.getAnnotation (Depends.class);
				if (annotation != null)
				{
					for (Class cl: annotation.value())
					{
						depends.add((ModelClass) parser.getElement(cl));
					}
				}
			}
		}
		return depends;
	}

	@Override
	public String getPackageDir( int scope) {
		if (underlyingClass == null)
			return null;
		else
			return  getPackagePrefix(scope).replace('.', java.io.File.separatorChar);
	}

	@Override
	public String getFile() {
		if (underlyingClass == null)
			return null;
		else
			return  removeMeta(underlyingClass.getName()).replace('.', java.io.File.separatorChar)+".java";
	}

	private String removeMeta(String name) {
		String s = name;
		if (isGenerated()) {
			if (name.endsWith("Meta"))
				name = name.substring(0, name.length() - 4);
			if (name.startsWith("Meta"))
				name = name.substring(4);
			int i = name.indexOf(".Meta");
			if (i >= 0)
				name = name.substring(0, i+1)+name.substring(i+5);
		}
		return name;
	}


	@Override
	public String getPackage() {
		if (underlyingClass == null)
			return null;
		else if (underlyingClass.getPackage() == null)
			return null;
		else
			return underlyingClass.getPackage().getName();
	}

	@Override
	public String getName() {
		String cl;
		if (underlyingClass != null)
			cl = underlyingClass.getSimpleName();
		else
			cl = objectClass.toString();
		cl = removeMeta(cl);
		return cl;
	}

	@Override
	public String getComments() {
		if (underlyingClass != null)
		{
			Description annotation = (Description) underlyingClass.getAnnotation (Description.class);
			if (annotation != null)
			{
				if (annotation.value() == null || annotation.value().length() == 0)
					return null;
				else
					return annotation.value();
			}
		}
		return null;
	}

	@Override
	public AbstractModelClass getSuperClass() {
		if (underlyingClass == null)
			return null;
		
		if (underlyingClass.getSuperclass() == null ||
				underlyingClass.getSuperclass() == Object.class)
			return null;
		else
			return (AbstractModelClass) parser.getElement(underlyingClass.getSuperclass());
	}

	@Override
	public String getPackagePrefix ()
	{
		if (getPackage() == null)
			return "";
		else
			return getPackage()+".";
			
	}
	@Override
	public String getFullName() {
		return getPackagePrefix()+getName();
	}

	@Override
	public String getImplFullName() {
		return getPackagePrefix()+getImplName();
	}
	
	@Override
	public String getFullName(int scope) {
		return getPackagePrefix(scope)+getName(scope);
	}

	@Override
	public String getImplFullName(int scope) {
		return getPackagePrefix(scope)+getImplName(scope);
	}
	
	@Override
	public String getImplName ()
	{
		return getName() + "Impl";
	}

	@Override
	public String getImplName (int scope)
	{
		return getName(scope) + "Impl";
	}

	@Override
	public String getSerialVersion() {
		if (underlyingClass == null)
			return "0";
		else
		{
			try {
				ValueObject vo = (ValueObject) underlyingClass.getAnnotation(ValueObject.class);
				if (vo != null)
				{
					return vo.serialVersion();
				}
				else
					return "1";
			} catch (Exception e) {
				return "1";
			}
		}
	}


	@Override
	public boolean isCollection()
	{
		return _collection;
	}

	@Override
	public String getJavaType() {
		Entity entity = (Entity) getAnnotation(Entity.class);
		if ((parser.isTranslateEntities() && isEntity() || parser.isTranslate()) &&  
			!entity.translatedName().isEmpty())
		{
			if (entity.translatedPackage().isEmpty())
				return getPackage()+"."+entity.translatedName();
			else
				return entity.translatedPackage()+"."+entity.translatedName();
		}
		else
			return removeMeta(javaType);
	}

	@Override
	public boolean isCriteria() {
		if (getAnnotation(Criteria.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public String getTenantFilter() {
		if (hasTenantAttribute())
			return "tenant.id";
		if (getAnnotation(Entity.class) != null)
		{
			String filter = ((Entity)getAnnotation(Entity.class)).tenantFilter();
			if (filter == null || filter.isEmpty())
				return null;
			else
				return filter.trim();
		}
		else
			return null;
	}

	@Override
	public String getJavaType(int scope) {
		if (isCollection() && _collectionClass != null && getChildClass() != null)
		{
			return _collectionClass.getCanonicalName()+"<"+getChildClass().getJavaType(scope)+">";
		}
		else if (isArray() && childClass != null)
		{
			return childClass.getJavaType(scope)+"[]";
		}
		else if (! Translate.mustTranslate(this, scope) || underlyingClass == null)
			return getJavaType();
		else 
		{
			Service service = (Service) underlyingClass.getAnnotation(Service.class);
			ValueObject vo = (ValueObject) underlyingClass.getAnnotation(ValueObject.class);
			Enumeration en = (Enumeration) underlyingClass.getAnnotation(Enumeration.class);
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			TranslatedClass tc = (TranslatedClass) underlyingClass.getAnnotation(TranslatedClass.class);
			if ( service != null && ! service.translatedName().isEmpty()  )
				return service.translatedPackage()+"."+service.translatedName();
			else if (vo != null && ! vo.translatedName().isEmpty())
				return vo.translatedPackage()+"."+vo.translatedName();
			else if (parser.isTranslateEntities() && entity != null && ! entity.translatedName().isEmpty())
				return entity.translatedPackage()+"."+entity.translatedName();
			else if (parser.isTranslate() && en != null && ! en.translatedName().isEmpty())
				return en.translatedPackage()+"."+en.translatedName();
			else if (parser.isTranslate() && tc != null && ! tc.name().isEmpty())
				return tc.pkg()+"."+tc.name();
			else
				return getJavaType(); 
		}
	}

	@Override
	public String getName(int scope) {
		if (!Translate.mustTranslate(this, scope) || underlyingClass == null)
			return getName ();
		else
		{
			Service service = (Service) underlyingClass.getAnnotation(Service.class);
			ValueObject vo = (ValueObject) underlyingClass.getAnnotation(ValueObject.class);
			Enumeration en = (Enumeration) underlyingClass.getAnnotation(Enumeration.class);
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			TranslatedClass tc = (TranslatedClass) underlyingClass.getAnnotation(TranslatedClass.class);
			if ( service != null && ! service.translatedName().isEmpty()  )
				return service.translatedName();
			else if (vo != null && ! vo.translatedName().isEmpty())
				return vo.translatedName();
			else if (entity != null && ! entity.translatedName().isEmpty())
				return entity.translatedName();
			else if (en != null && ! en.translatedName().isEmpty())
				return en.translatedName();
			else if (tc != null && ! tc.name().isEmpty())
				return tc.name();
			else
				return getName(); 
		}
	}
	
	
	@Override
	public String getPackagePrefix(int scope) {
		if (!Translate.mustTranslate(this, scope) || underlyingClass == null)
			return getPackagePrefix();
		else
		{
			Service service = (Service) underlyingClass.getAnnotation(Service.class);
			ValueObject vo = (ValueObject) underlyingClass.getAnnotation(ValueObject.class);
			Enumeration en = (Enumeration) underlyingClass.getAnnotation(Enumeration.class);
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			TranslatedClass tc = (TranslatedClass) underlyingClass.getAnnotation(TranslatedClass.class);
			if ( service != null && ! service.translatedName().isEmpty()  )
				return service.translatedPackage()+".";
			else if (vo != null && ! vo.translatedName().isEmpty())
				return vo.translatedPackage()+".";
			else if (entity != null && ! entity.translatedName().isEmpty())
				return entity.translatedPackage()+".";
			else if (en != null && ! en.translatedName().isEmpty())
				return en.translatedPackage()+".";
			else if (tc != null && ! tc.pkg().isEmpty())
				return tc.pkg()+".";
			else
				return getPackagePrefix(); 
		}
	}

	@Override
	public String getPackage(int scope) {
		if (!Translate.mustTranslate(this, scope) || underlyingClass == null)
			return getPackage();
		else
		{
			Service service = (Service) underlyingClass.getAnnotation(Service.class);
			ValueObject vo = (ValueObject) underlyingClass.getAnnotation(ValueObject.class);
			Enumeration en = (Enumeration) underlyingClass.getAnnotation(Enumeration.class);
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			TranslatedClass tc = (TranslatedClass) underlyingClass.getAnnotation(TranslatedClass.class);
			if ( service != null && ! service.translatedName().isEmpty()  )
				return service.translatedPackage();
			else if (vo != null && ! vo.translatedName().isEmpty())
				return vo.translatedPackage();
			else if (entity != null && ! entity.translatedName().isEmpty())
				return entity.translatedPackage();
			else if (en != null && ! en.translatedName().isEmpty())
				return en.translatedPackage();
			else if (tc != null && ! tc.pkg().isEmpty())
				return tc.pkg();
			else
				return getPackage(); 
		}
	}

	@Override
	public void setGenerated(boolean b) {
		this.generated = b;
		
	}

	@Override
	public void fixup() {
		parseObjectClass(parser);		

		AbstractModelClass mc = getSuperClass();
		if (mc != null && ! mc.getSpecializations().contains(this))
			mc.getSpecializations().add(this);
		if (isEntity())
		{
			for (AbstractModelAttribute att: new LinkedList<AbstractModelAttribute>(getAttributes()))
			{
				if (att.getDataType().isEntity() && att.getDataType() != this)
				{
					if ( ! getDepends().contains(att.getDataType()))
						getDepends().add(att.getDataType());
				} 
				else if (att.getDataType().isCollection() && att.getDataType().getChildClass().isEntity() &&
						att.getDataType().getChildClass() != this)
				{
					if (! getDepends().contains(att.getDataType().getChildClass()))
						getDepends().add(att.getDataType().getChildClass());
				}
				AbstractModelAttribute ra = att.getReverseAttribute();
				if (ra != null)
				{
					AbstractModelClass foreignClass = ra.getModelClass();
					if (! foreignClass.getAttributes().contains(ra))
					{
						foreignClass.getAttributes().add(ra);
						if (foreignClass != this && ! foreignClass.getDepends().contains(this))
							foreignClass.getDepends().add(this);
					}
				}
				if (att.getDataType().isEntity() && ! att.getDataType().getForeignKeys().contains(att))
					att.getDataType().getForeignKeys().add(att);
			}
//			sortAllAttributes();
//			sortAttributes();
		}

		for (AbstractModelClass depend: getDepends())
		{
			if (! depend.getProvides().contains(this))
			{
				depend.getProvides().add(this);
				sortProvides();
			}
				
		}

		sortDepends();
		sortProvides();
	}


	private void sortDepends() {
		Collections.sort(getDepends(), new Comparator<AbstractModelClass>() {
			public int compare(AbstractModelClass o1,
					AbstractModelClass o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
	}

	private void sortProvides() {
		Collections.sort(getProvides(), new Comparator<AbstractModelClass>() {
			public int compare(AbstractModelClass o1,
					AbstractModelClass o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
	}

	private void sortAttributes() {
		Collections.sort(attributes, new Comparator<AbstractModelAttribute>() {

			public int compare(AbstractModelAttribute o1,
					AbstractModelAttribute o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
	}


	private void sortAllAttributes() {
		Collections.sort(allAttributes, new Comparator<AbstractModelAttribute>() {

			public int compare(AbstractModelAttribute o1,
					AbstractModelAttribute o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
	}

	@Override
	public AbstractModelAttribute getIdentifier ()
	{
		for (AbstractModelAttribute att: getAttributes())
		{
			if (att.isIdentifier())
				return att;
		}
		if (getSuperClass() != null)
			return getSuperClass().getIdentifier();
		else
			throw new RuntimeException ("Missing identifier for entity "+getName());
	}

	@Override
	public String getDaoName(int scope) {
		return getName (scope)+"Dao";
	}

	@Override
	public String getDaoFullName(int scope) {
		return getPackagePrefix(scope)+getDaoName(scope);
	}

	@Override
	public boolean hasNonStaticMethods() {
		for (ModelOperation op: getOperations())
		{
			if (! op.isQuery() && ! op.isStatic())
				return true;
		}
		return false;
	}

	@Override
	public String getDaoImplName(int scope) {
		return getName(scope)+"DaoImpl";
	}

	@Override
	public String getDaoBaseName(int scope) {
		return getName(scope)+"DaoBase";
	}

	@Override
	public String getVarName() {
		return Util.firstLower(getName());
	}

	@Override
	public boolean isPrimitive() {
		return underlyingClass != null && underlyingClass.isPrimitive();
	}

	@Override
	public boolean isString() {
		return String.class == underlyingClass;
	}

	@Override
	public boolean isVoid() {
		return void.class == underlyingClass;
	}

	@Override
	public String getDaoImplFullName(int scope) {
		return getPackagePrefix(scope)+getDaoImplName(scope);
	}

	@Override
	public String getTableName() {
		if (underlyingClass != null)
		{
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			if (entity != null && entity.table().length() > 0)
				return entity.table();
		}
		return null;
	}

	@Override
	public String getDiscriminatorValue () {
		if (underlyingClass != null)
		{
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			if (entity != null && entity.discriminatorValue().length() > 0)
				return entity.discriminatorValue();
		}
		return null;
	}

	@Override
	public String getDiscriminatorColumn() {
		if (underlyingClass != null)
		{
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			if (entity != null && entity.discriminatorColumn().length() > 0)
				return entity.discriminatorColumn();
		}
		return null;
	}

	@Override
	public boolean isAbstract() {
		return underlyingClass != null && Modifier.isAbstract(underlyingClass.getModifiers());
	}
	
	@Override
	public boolean isInternal()
	{
		if (underlyingClass == null)
			return false;
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.internal();
		else
			return false;
	}

	@Override
	public boolean isStateful()
	{
		if (underlyingClass == null)
			return false;
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.stateful();
		else
			return false;
	}

	@Override
	public boolean isServerOnly()
	{
		if (underlyingClass == null)
			return false;
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.serverOnly();
		else
			return false;
	}

	@Override
	public boolean isConsoleOnly()
	{
		if (underlyingClass == null)
			return false;
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.consoleOnly();
		else
			return false;
	}

	@Override
	public String getServerPath()
	{
		if (underlyingClass == null)
			return null;
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.serverPath();
		else
			return null;
	}

	@Override
	public String getServerRole ()
	{
		if (underlyingClass == null)
			return null;
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.serverRole();
		else
			return null;
	}

	Set<AbstractModelClass> allActors = null;
	@Override
	public Set<AbstractModelClass> getAllActors() {
		if (allActors == null)
		{
			allActors = new HashSet<AbstractModelClass>();
			allActors.addAll(getActors());
			for (ModelOperation op: getOperations())
			{
				allActors.addAll(op.getActors());
			}
		}
		return allActors;
	}

	List<AbstractModelClass> actors = null;
	private LinkedList<AbstractModelAttribute> allAttributes;
	@Override
	public List<AbstractModelClass> getActors() {
		if (actors == null)
		{
			actors = new LinkedList<AbstractModelClass>();
			Service op = (Service) underlyingClass.getAnnotation(Service.class);
			if (op != null)
			{
				for (Class actor: op.grantees())
				{
					actors.add( (ModelClass) parser.getElement(actor));
				}
			}
		}
		return actors;
	}

	@Override
	public String getBeanName(int scope) {
		return getName(scope)+"Bean";
	}

	@Override
	public String getEjbInterfaceFullName(int scope) {
		return getEjbPackage(scope)+"."+getName(scope);
	}

	@Override
	public String getEjbPackage(int scope) {
		return getPackage(scope) + ".ejb";
	}

	@Override
	public String getEjbHomeFullName(int scope) {
		return getEjbPackage(scope)+"."+getName(scope)+"Home";
	}

	@Override
	public boolean isTranslated() {
		if (underlyingClass == null)
			return false;
		
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		ValueObject vo = (ValueObject) underlyingClass.getAnnotation(ValueObject.class);
		Enumeration en = (Enumeration) underlyingClass.getAnnotation(Enumeration.class);
		Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
		TranslatedClass tc = (TranslatedClass) underlyingClass.getAnnotation(TranslatedClass.class);
		if (getFullName(Translate.DONT_TRANSLATE).equals(getFullName(Translate.TRANSLATE)))
			return false;
		else
			return true; 
	}

	@Override
	public String getSpringBeanName(Generator generator, int scope) {
		String name = "";
		String suffix = "";
		if (generator.isPlugin() && isGenerated())
		{
			name = generator.getPluginName() + "-";
		} 
		else if (isService() &&
				!generator.isTransaltedOnly() &&
				scope == (generator.isTranslated() ? Translate.SERVICE_SCOPE: Translate.ALTSERVICE_SCOPE))
		{
			suffix = "-v2";
		}
		if (isEntity())
			name = name + Util.firstLower(getDaoName(scope));
		else
			name = name + Util.firstLower(getName(scope));
		return name + suffix;
	}

	@Override
	public String getEjbName(Generator generator, int scope) {
		if (Translate.mustTranslate(this, scope) && 
				!generator.isTransaltedOnly())
			return getName(scope)+"-v2";
		else
			return getName(scope);
	}

	@Override
	public String getBeanFullName(int scope) {
		return getEjbPackage(scope)+"."+getBeanName(scope);
	}

	@Override
	public String getRoleName() {
		if (underlyingClass == null)
			return null;
		
		Role r = (Role) underlyingClass.getAnnotation(Role.class);
		if (r == null)
			return null;
		else
			return r.name().replaceAll("\n", "").trim();
	}

	@Override
	public String getRawType() {
		if (objectClass instanceof Class)
			return javaType;
		else if (objectClass instanceof ParameterizedType)
		{
			Type rawType = ((ParameterizedType) objectClass).getRawType();
			if (rawType instanceof Class)
				return ((Class)rawType).getCanonicalName();
			else
				return rawType.toString();
		} 
		else if (objectClass instanceof GenericArrayType)
		{
			return objectClass.toString();
		}
		else
			return null;
	}

	@Override
	public String getLocalServiceName(int scope) {
		return Util.firstLower(getName(scope));
	}

	@Override
	public String getBaseName(int scope) {
		if (isEntity())
			return getDaoName(scope) + "Base";
		else
			return getName(scope) + "Base";
	}

	@Override
	public String getBaseFullName(int scope) {
		return getPackagePrefix(scope)+getBaseName(scope);
	}

	@Override
	public List<AbstractModelAttribute> getAllAttributes ()
	{
		if (allAttributes == null)
		{
			allAttributes = new LinkedList<AbstractModelAttribute>();
			
			AbstractModelClass m = getSuperClass();
			if ( m != null)
				allAttributes.addAll (m.getAllAttributes());
			allAttributes.addAll(getAttributes());

//			sortAllAttributes();

		}
		return allAttributes;
	}

	@Override
	public String getXmlId() {
		return "class."+getFullName().replace('<', '_').replace('"', '_').replace('>', '_');
	}

	@Override
	public boolean isException() {
		if (getAnnotation(ApplicationException.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public long lastModified() {
		Class cl;
		if (isCollection())
			cl = getChildClass().underlyingClass;
		else
			cl = underlyingClass;
		if (cl != null)
		{
			URL u = cl.getClassLoader().getResource(cl.getName().replace('.', '/')+".class");
			if (u != null && u.getProtocol().equals("file"))
			{
				File f;
				try {
					f = new File (u.toURI());
					if (f.canRead())
						return f.lastModified();
				} catch (URISyntaxException e) {
				}
			}
		}
		return 0;
	}

	@Override
	public String getFile(int scope) {
		if (underlyingClass == null)
			return null;
		else
			return  getFullName(scope).replace('.', java.io.File.separatorChar)+".java";
	}

	public String getDescription() {
		Description desc = (Description) getAnnotation(Description.class);
		if (desc != null)
			return desc.value();
		else
			return "";
	}


	@Override
	public JsonObject getJsonObject() {
		if (jsonObject != null)
			return jsonObject;
		else if (underlyingClass == null)
			return null;
		else
			return (JsonObject) underlyingClass.getAnnotation(JsonObject.class);
	}


	@Override
	public boolean isTranslatedImpl() {
		if (underlyingClass == null)
			return false;
		
		Service service = (Service) underlyingClass.getAnnotation(Service.class);
		if (service != null)
			return service.translatedImpl();
		else
			return false;
	}

	Boolean hasTenant = null;
	private JsonObject jsonObject;
	@Override
	public boolean hasTenantAttribute() {
		if (hasTenant != null)
			return hasTenant.booleanValue();
		for (AbstractModelAttribute att: getAllAttributes())
		{
			if (att.getName().equals("tenant"))
			{
				hasTenant = Boolean.TRUE;
				return hasTenant.booleanValue();
			}
		}
		hasTenant = Boolean.FALSE;
		return hasTenant.booleanValue();
	}

	public int getCache() {
		ValueObject ann = (ValueObject) getAnnotation(ValueObject.class); 
		if ( ann != null)
			return ann.cache();
		else
			return 0;
	}

	public boolean isSimple() {
		Service ann = (Service) getAnnotation(Service.class); 
		if ( ann != null)
			return ann.simple();
		else
			return false;
	}


	@Override
	public boolean isFuture() {
		return _future;
	}


	@Override
	public String getSinceAttribute() {
		Entity e = (Entity)getAnnotation(Entity.class);
		return e != null && !e.since().isEmpty() ? e.since(): null;
	}


	@Override
	public String getUntilAttribute() {
		Entity e = (Entity)getAnnotation(Entity.class);
		return e != null && !e.until().isEmpty() ? e.until(): null;
	}


	@Override
	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}
}
