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

public class ModelClass extends ModelElement {

	private Type objectClass;
	private List<ModelOperation> operations;
	private LinkedList<ModelAttribute> attributes;
	private LinkedList<ModelClass> depends;
	private LinkedList<ModelClass> provides = new LinkedList<ModelClass>();
	private LinkedList<ModelClass> specializations = new LinkedList<ModelClass>();
	private LinkedList<ModelAttribute> foreignKeys = new LinkedList<ModelAttribute>();
	
	public LinkedList<ModelAttribute> getForeignKeys() {
		return foreignKeys;
	}

	public LinkedList<ModelClass> getProvides() {
		return provides;
	}

	public LinkedList<ModelClass> getSpecializations() {
		return specializations;
	}

	private Class underlyingClass;
	private boolean _collection;
	ModelClass childClass = null;
	private String javaType;

	public boolean isGenerated() {
		return generated;
	}

	private boolean generated;
	private boolean _array;
	private Class _collectionClass;

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
	
	public boolean isEntity ()
	{
		if (getAnnotation(Entity.class) != null)
			return true;
		else
			return false;
	}


	public boolean isService ()
	{
		if (getAnnotation(Service.class) != null)
			return true;
		else
			return false;
	}
	

	public boolean isValueObject ()
	{
		if (getAnnotation(ValueObject.class) != null)
			return true;
		else
			return false;
	}

	public boolean isEnumeration ()
	{
		if (getAnnotation(Enumeration.class) != null)
			return true;
		else
			return false;
	}

	public boolean isRole ()
	{
		if (getAnnotation(Role.class) != null)
			return true;
		else
			return false;
	}

	public boolean isIndex ()
	{
		if (getAnnotation(Index.class) != null)
			return true;
		else
			return false;
	}
	
	public String[] getIndexColumns ()
	{
		Index index = (Index) getAnnotation(Index.class);
		if (index != null)
			return index.columns();
		else
			return null;
	}
	
	public ModelClass getIndexEntity ()
	{
		Index index = (Index) getAnnotation(Index.class);
		if (index != null)
		{
			Class cl = index.entity();
			return (ModelClass) parser.getElement(cl);
		}
		else
			return null;
	}
	
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

	
	public List<ModelAttribute> getAttributes ()
	{
		if (attributes == null)
		{
			attributes = new LinkedList<ModelAttribute>();
		
			if (underlyingClass != null)
			{
				for (Field f: underlyingClass.getDeclaredFields())
				{
					attributes.add((ModelAttribute) parser.getElement(f));
				}
			}
		}
		return attributes;
	}

	public List<ModelClass> getDepends ()
	{
		if (depends == null)
		{
			depends = new LinkedList<ModelClass>();
		
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

	public String getPackageDir( boolean translated) {
		if (underlyingClass == null)
			return null;
		else
			return  getPackagePrefix(translated).replace('.', java.io.File.separatorChar);
	}

	public String getFile() {
		if (underlyingClass == null)
			return null;
		else
			return  underlyingClass.getName().replace('.', java.io.File.separatorChar)+".java";
	}

	public String getPackage() {
		if (underlyingClass == null)
			return null;
		else if (underlyingClass.getPackage() == null)
			return null;
		else
			return underlyingClass.getPackage().getName();
	}

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

	public ModelClass getSuperClass() {
		if (underlyingClass == null)
			return null;
		
		if (underlyingClass.getSuperclass() == null ||
				underlyingClass.getSuperclass() == Object.class)
			return null;
		else
			return (ModelClass) parser.getElement(underlyingClass.getSuperclass());
	}

	public String getPackagePrefix ()
	{
		if (getPackage() == null)
			return "";
		else
			return getPackage()+".";
			
	}
	public String getFullName() {
		return getPackagePrefix()+getName();
	}

	public String getImplFullName() {
		return getPackagePrefix()+getImplName();
	}
	
	public String getFullName(boolean translated) {
		return getPackagePrefix(translated)+getName(translated);
	}

	public String getImplFullName(boolean translated) {
		return getPackagePrefix(translated)+getImplName(translated);
	}
	
	public String getImplName ()
	{
		return getName() + "Impl";
	}

	public String getImplName (boolean translated)
	{
		return getName(translated) + "Impl";
	}

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


	public boolean isCollection()
	{
		return _collection;
	}

	public String getJavaType() {
		return javaType;
	}

	public boolean isCriteria() {
		if (getAnnotation(Criteria.class) != null)
			return true;
		else
			return false;
	}

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

	public void setGenerated(boolean b) {
		this.generated = b;
		
	}

	public void fixup() {
		ModelClass mc = getSuperClass();
		if (mc != null && ! mc.getSpecializations().contains(this))
			mc.getSpecializations().add(this);
		for (ModelClass depend: getDepends())
		{
			if (! depend.getProvides().contains(this))
				depend.getProvides().add(this);
		}
		if (isEntity())
		{
			for (ModelAttribute att: getAttributes())
			{
				if (att.getDataType().isEntity() && ! att.getDataType().foreignKeys.contains(att))
					att.getDataType().foreignKeys.add(att);
			}
		}
	}
	
	public ModelAttribute getIdentifier ()
	{
		for (ModelAttribute att: getAttributes())
		{
			if (att.isIdentifier())
				return att;
		}
		if (getSuperClass() != null)
			return getSuperClass().getIdentifier();
		else
			throw new RuntimeException ("Missing identifier for entity "+getName());
	}

	public String getDaoName(boolean translated) {
		return getName (translated)+"Dao";
	}

	public String getDaoFullName(boolean translated) {
		return getPackagePrefix(translated)+getDaoName(translated);
	}

	public boolean hasNonStaticMethods() {
		for (ModelOperation op: getOperations())
		{
			if (! op.isQuery() && ! op.isStatic())
				return true;
		}
		return false;
	}

	public String getDaoImplName(boolean translated) {
		return getName(translated)+"DaoImpl";
	}

	public String getDaoBaseName(boolean translated) {
		return getName(translated)+"DaoBase";
	}

	public String getVarName() {
		return Util.firstLower(getName());
	}

	public boolean isPrimitive() {
		return underlyingClass != null && underlyingClass.isPrimitive();
	}

	public boolean isString() {
		return String.class == underlyingClass;
	}

	public boolean isVoid() {
		return void.class == underlyingClass;
	}

	public String getDaoImplFullName(boolean translated) {
		return getPackagePrefix(translated)+getDaoImplName(translated);
	}

	public String getTableName() {
		if (underlyingClass != null)
		{
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			if (entity != null && entity.table().length() > 0)
				return entity.table();
		}
		return null;
	}

	public String getDiscriminatorValue () {
		if (underlyingClass != null)
		{
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			if (entity != null && entity.discriminatorValue().length() > 0)
				return entity.discriminatorValue();
		}
		return null;
	}

	public String getDiscriminatorColumn() {
		if (underlyingClass != null)
		{
			Entity entity = (Entity) underlyingClass.getAnnotation(Entity.class);
			if (entity != null && entity.discriminatorColumn().length() > 0)
				return entity.discriminatorColumn();
		}
		return null;
	}

	public boolean isAbstract() {
		return underlyingClass != null && Modifier.isAbstract(underlyingClass.getModifiers());
	}
	
	public ModelAttribute searchForeignKey (ModelClass foreignClass, ModelAttribute foreignAttribute)
	{
		if (foreignClass == null || foreignAttribute == null)
			return null;
		String column = foreignAttribute.getForeignKey();

		
		for (ModelAttribute att2: getAttributes())
		{
			if (column.equals(att2.getColumn()))
			{
				return att2;
			}
		}
		return null;
	}
	
	
	public ModelAttribute searchReverseForeignKey (ModelClass foreignClass, ModelAttribute foreignAttribute)
	{
		if (foreignClass == null || foreignAttribute == null)
			return null;
		String column = foreignAttribute.getColumn();

		
		for (ModelAttribute att2: getAttributes())
		{
			if (column.equals(att2.getForeignKey()))
			{
				return att2;
			}
		}
		return null;
	}
	
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

	Set<ModelClass> allActors = null;
	public Set<ModelClass> getAllActors() {
		if (allActors == null)
		{
			allActors = new HashSet<ModelClass>();
			allActors.addAll(getActors());
			for (ModelOperation op: getOperations())
			{
				allActors.addAll(op.getActors());
			}
		}
		return allActors;
	}

	List<ModelClass> actors = null;
	private LinkedList<ModelAttribute> allAttributes;
	public boolean left;
	public List<ModelClass> getActors() {
		if (actors == null)
		{
			actors = new LinkedList<ModelClass>();
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

	public String getBeanName(boolean translated) {
		return getName(translated)+"Bean";
	}

	public String getEjbInterfaceFullName(boolean translated) {
		return getEjbPackage(translated)+"."+getName(translated);
	}

	public String getEjbPackage(boolean translated) {
		return getPackage(translated) + ".ejb";
	}

	public String getEjbHomeFullName(boolean translated) {
		return getEjbPackage(translated)+"."+getName(translated)+"Home";
	}

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

	public String getEjbName(boolean translated) {
		if (translated)
			return getName(false)+"-translated";
		else
			return getName(false);
	}

	public String getBeanFullName(boolean translated) {
		return getEjbPackage(translated)+"."+getBeanName(translated);
	}

	public String getRoleName() {
		if (underlyingClass == null)
			return null;
		
		Role r = (Role) underlyingClass.getAnnotation(Role.class);
		if (r == null)
			return null;
		else
			return r.name();
	}

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

	public String getLocalServiceName(boolean translated) {
		return Util.firstLower(getName(translated));
	}

	public String getBaseName(boolean translated) {
		if (isEntity())
			return getDaoName(translated) + "Base";
		else
			return getName(translated) + "Base";
	}

	public String getBaseFullName(boolean translated) {
		return getPackagePrefix(translated)+getBaseName(translated);
	}

	public List<ModelAttribute> getAllAttributes ()
	{
		if (allAttributes == null)
		{
			allAttributes = new LinkedList<ModelAttribute>();
			
			ModelClass m = getSuperClass();
			if ( m != null)
				allAttributes.addAll (m.getAllAttributes());
			allAttributes.addAll(getAttributes());
		}
		return allAttributes;
	}

	public String getXmlId() {
		return "class."+getFullName().replace('<', '_').replace('"', '_').replace('>', '_');
	}

	public boolean isException() {
		if (getAnnotation(ApplicationException.class) != null)
			return true;
		else
			return false;
	}

	public String generatePlantUml(boolean translated, boolean attributes, boolean methods)
	{
		return generatePlantUml(translated, attributes, methods, "");
	}
	
	public String generatePlantUml(boolean translated, boolean attributes, boolean methods, String extraAttributes)
	{
		StringBuffer b = new StringBuffer();
		if (isRole())
		{
			b.append("actor "+getName(translated));
		}
		else if (isEntity ())
		{
			b.append ("class "+getName(translated)+" <<(E,#ff4040) Entity>>");
		} 
		else if (isService())
		{
			b.append ("class "+getName(translated)+" <<(S,#ffff00) Service>>");
		}
		else if (isEnumeration())
		{
			b.append ("class "+getName(translated)+" <<(E,#ffff00) Enumeration>>");
		}
		else if (isValueObject())
		{
			b.append ("class "+getName(translated)+" <<(V,#4040ff) ValueObject>>");
		}
		b.append(extraAttributes);
		if (isGenerated() && (attributes || methods))
		{			
			b.append ("{\n");
			if (attributes)
				for (ModelAttribute attribute: getAttributes())
				{
					b.append ("  ")
						.append (attribute.getName(translated))
						.append(":")
						.append (attribute.getDataType().getJavaType(true));
					if (!attribute.isRequired())
						b.append (" \"0..1\"");
					b.append ("\n");
				}
			if (methods)
				for (ModelOperation operation: getOperations())
				{
					b.append ("  ")
					.append (operation.getName(translated))
					.append("(");
					boolean first = true;
					for (ModelParameter p: operation.getParameters())
					{
						if (!first) b.append(",");
						first = false;
						b.append (p.getName(translated));
						if (!p.isRequired())
							b.append (" \"0..1\"");
						
					}
					b.append(")\n");
				}
			b.append ("}");
		}
		b.append ("\n");
		
		return b.toString();
	}

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

	public String getFile(boolean translated) {
		if (underlyingClass == null)
			return null;
		else
			return  getFullName(translated).replace('.', java.io.File.separatorChar)+".java";
	}

	public String getJavaType(boolean translated, boolean translatedOnly) {
		if (translatedOnly || isValueObject() || isService() ||
				(isCollection() && getChildClass() != null && (getChildClass().isValueObject() || getChildClass().isEntity())))
		{
			return getJavaType (translated);
		}
		else
			return getJavaType (false);
	}

}
