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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.text.StyledEditorKit.UnderlineAction;

import com.soffid.mda.annotation.ApplicationException;
import com.soffid.mda.annotation.Criteria;
import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.Entity;
import com.soffid.mda.annotation.Enumeration;
import com.soffid.mda.annotation.ForeignKey;
import com.soffid.mda.annotation.Index;
import com.soffid.mda.annotation.Operation;
import com.soffid.mda.annotation.Role;
import com.soffid.mda.annotation.Service;
import com.soffid.mda.annotation.TranslatedClass;
import com.soffid.mda.annotation.ValueObject;
import com.soffid.mda.generator.Generator;
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
	ModelClass childClass = null;
	private String javaType;

	@Override
	public boolean isGenerated() {
		return generated;
	}

	private boolean generated;
	private boolean _array;
	private Class _collectionClass;

	@Override
	public boolean isArray() {
		return _array;
	}

	public ModelClass(Parser parser, Type classFile) {
		super (parser);
		objectClass = classFile;
		underlyingClass = null;
		_collection = false;
		
		if (objectClass instanceof Class)
		{
			underlyingClass = (Class) objectClass;
			
			
			javaType = underlyingClass.getCanonicalName();
			if (underlyingClass.isArray() )
			{
				_array = true;
				childClass =  (ModelClass) parser.getElement(underlyingClass.getComponentType());
			}
		}
		else if (objectClass instanceof ParameterizedType)
		{
			Type rawType = ((ParameterizedType) objectClass).getRawType();
			javaType = objectClass.toString();
			if (rawType instanceof Class)
			{
				if (Collection.class.isAssignableFrom((Class) rawType))
				{
					_collection = true;
					_collectionClass = (Class) rawType;
					if (((ParameterizedType) objectClass).getActualTypeArguments().length > 0)
					{
						Type pt = ((ParameterizedType) objectClass).getActualTypeArguments()[0];
						if (pt instanceof Class)
						{
							childClass =  (ModelClass) parser.getElement( pt );
						}
					}
				}
				else
					underlyingClass = (Class) rawType;
			}
		}
		else if (objectClass instanceof GenericArrayType)
		{
			javaType = objectClass.toString();
			_array = true;
			childClass = (ModelClass) parser.getElement(((GenericArrayType) objectClass).getGenericComponentType()); 
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
	public String getPackageDir( boolean translated) {
		if (underlyingClass == null)
			return null;
		else
			return  getPackagePrefix(translated).replace('.', java.io.File.separatorChar);
	}

	@Override
	public String getFile() {
		if (underlyingClass == null)
			return null;
		else
			return  underlyingClass.getName().replace('.', java.io.File.separatorChar)+".java";
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
		if (underlyingClass != null)
			return underlyingClass.getSimpleName();
		else
			return objectClass.toString();
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
	public String getFullName(boolean translated) {
		return getPackagePrefix(translated)+getName(translated);
	}

	@Override
	public String getImplFullName(boolean translated) {
		return getPackagePrefix(translated)+getImplName(translated);
	}
	
	@Override
	public String getImplName ()
	{
		return getName() + "Impl";
	}

	@Override
	public String getImplName (boolean translated)
	{
		return getName(translated) + "Impl";
	}

	@Override
	public String getSerialVersion() {
		if (underlyingClass == null)
			return "0";
		else
		{
			try {
				Field f = underlyingClass.getField("serialVersion");
				return f.get(null).toString();
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
		return javaType;
	}

	@Override
	public boolean isCriteria() {
		if (getAnnotation(Criteria.class) != null)
			return true;
		else
			return false;
	}

	@Override
	public String getJavaType(boolean translated) {
		if (isCollection() && _collectionClass != null && getChildClass() != null)
		{
			return _collectionClass.getCanonicalName()+"<"+getChildClass().getJavaType(translated)+">";
		}
		else if (!translated || underlyingClass == null)
			return getJavaType ();
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
			else if (parser.isTranslateOnly() && entity != null && ! entity.translatedName().isEmpty())
				return entity.translatedPackage()+"."+entity.translatedName();
			else if (parser.isTranslateOnly() && en != null && ! en.translatedName().isEmpty())
				return en.translatedPackage()+"."+en.translatedName();
			else if (parser.isTranslateOnly() && tc != null && ! tc.name().isEmpty())
				return tc.pkg()+"."+tc.name();
			else
				return getJavaType(); 
		}
	}

	@Override
	public String getName(boolean translated) {
		if (!translated || underlyingClass == null)
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
	public String getPackagePrefix(boolean translated) {
		if (!translated || underlyingClass == null)
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
	public String getPackage(boolean translated) {
		if (!translated || underlyingClass == null)
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
		}

		for (AbstractModelClass depend: getDepends())
		{
			if (! depend.getProvides().contains(this))
				depend.getProvides().add(this);
		}
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
	public String getDaoName(boolean translated) {
		return getName (translated)+"Dao";
	}

	@Override
	public String getDaoFullName(boolean translated) {
		return getPackagePrefix(translated)+getDaoName(translated);
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
	public String getDaoImplName(boolean translated) {
		return getName(translated)+"DaoImpl";
	}

	@Override
	public String getDaoBaseName(boolean translated) {
		return getName(translated)+"DaoBase";
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
	public String getDaoImplFullName(boolean translated) {
		return getPackagePrefix(translated)+getDaoImplName(translated);
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
	public String getBeanName(boolean translated) {
		return getName(translated)+"Bean";
	}

	@Override
	public String getEjbInterfaceFullName(boolean translated) {
		return getEjbPackage(translated)+"."+getName(translated);
	}

	@Override
	public String getEjbPackage(boolean translated) {
		return getPackage(translated) + ".ejb";
	}

	@Override
	public String getEjbHomeFullName(boolean translated) {
		return getEjbPackage(translated)+"."+getName(translated)+"Home";
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
		if (getFullName(false).equals(getFullName(true)))
			return false;
		if ( service != null && ! service.translatedName().isEmpty()  )
			return true;
		else if (vo != null && ! vo.translatedName().isEmpty())
			return true;
		else if (en != null && ! en.translatedName().isEmpty())
			return true;
		else if (entity != null && ! entity.translatedName().isEmpty())
			return true;
		else if (tc != null && ! tc.name().isEmpty())
			return true;
		else
			return false; 
	}

	@Override
	public String getSpringBeanName(Generator generator, boolean translated) {
		String name = "";
		if (generator.isPlugin() && isGenerated())
		{
			name = generator.getPluginName() + "-";
		}
		if (isEntity())
			name = name + Util.firstLower(getDaoName(translated));
		else
			name = name + Util.firstLower(getName(translated));
		return name;
	}

	@Override
	public String getEjbName(boolean translated) {
		if (translated)
			return getName(false)+"-translated";
		else
			return getName(false);
	}

	@Override
	public String getBeanFullName(boolean translated) {
		return getEjbPackage(translated)+"."+getBeanName(translated);
	}

	@Override
	public String getRoleName() {
		if (underlyingClass == null)
			return null;
		
		Role r = (Role) underlyingClass.getAnnotation(Role.class);
		if (r == null)
			return null;
		else
			return r.name();
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
	public String getLocalServiceName(boolean translated) {
		return Util.firstLower(getName(translated));
	}

	@Override
	public String getBaseName(boolean translated) {
		if (isEntity())
			return getDaoName(translated) + "Base";
		else
			return getName(translated) + "Base";
	}

	@Override
	public String getBaseFullName(boolean translated) {
		return getPackagePrefix(translated)+getBaseName(translated);
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
	public String getFile(boolean translated) {
		if (underlyingClass == null)
			return null;
		else
			return  getFullName(translated).replace('.', java.io.File.separatorChar)+".java";
	}

	@Override
	public String getJavaType(boolean translated, boolean translatedOnly) {
		if (translatedOnly || isValueObject() || isService() ||
				(isCollection() && getChildClass() != null && (getChildClass().isValueObject() || getChildClass().isEntity())))
		{
			return getJavaType (translated);
		}
		else
			return getJavaType (false);
	}

	public String getDescription() {
		Description desc = (Description) getAnnotation(Description.class);
		if (desc != null)
			return desc.value();
		else
			return "";
	}


	Boolean hasTenant = null;
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

}
