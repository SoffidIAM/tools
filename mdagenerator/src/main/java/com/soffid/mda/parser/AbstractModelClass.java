package com.soffid.mda.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.soffid.mda.annotation.JsonObject;
import com.soffid.mda.generator.DocGenerator;
import com.soffid.mda.generator.Generator;
import com.soffid.mda.generator.Translate;
import com.soffid.mda.generator.Util;

public abstract class AbstractModelClass extends ModelElement {
	public boolean left;

	public AbstractModelClass(Parser parser) {
		super(parser);
	}


	public abstract String getJavaType(int scope);

	public abstract String getFile(int scope);

	public abstract long lastModified();

	public abstract boolean isException();

	public abstract String getXmlId();

	public abstract List<AbstractModelAttribute> getAllAttributes();

	public abstract String getBaseFullName(int scope);

	public abstract String getBaseName(int scope);

	public abstract String getLocalServiceName(int scope);

	public abstract String getRawType();

	public abstract String getRoleName();

	public abstract String getBeanFullName(int scope);

	public abstract String getEjbName(int scope);

	public abstract String getSpringBeanName(Generator generator, int scope);

	public abstract boolean isTranslated();
	
	public abstract boolean isTranslatedImpl ();

	public abstract String getEjbHomeFullName(int scope);

	public abstract String getEjbPackage(int scope);

	public abstract String getEjbInterfaceFullName(int scope);

	public abstract String getBeanName(int scope);

	public abstract List<AbstractModelClass> getActors();

	public abstract Set<AbstractModelClass> getAllActors();

	public abstract String getServerRole();

	public abstract String getServerPath();

	public abstract boolean isServerOnly();

	public abstract boolean isInternal();

	public abstract boolean isAbstract();

	public abstract String getDiscriminatorColumn();

	public abstract String getDiscriminatorValue();

	public abstract String getTableName();

	public abstract String getDaoImplFullName(int scope);

	public abstract boolean isVoid();

	public abstract boolean isString();

	public abstract boolean isPrimitive();

	public abstract String getVarName();

	public abstract String getDaoBaseName(int scope);

	public abstract String getDaoImplName(int scope);

	public abstract boolean hasNonStaticMethods();

	public abstract String getDaoFullName(int scope);

	public abstract String getDaoName(int scope);

	public abstract AbstractModelAttribute getIdentifier();

	public abstract void fixup();

	public abstract void setGenerated(boolean b);

	public abstract String getPackage(int scope);

	public abstract String getPackagePrefix(int scope);

	public abstract String getName(int scope);

	public String getJsonMapperName(int scope) {
		return getName(scope)+"JsonMapper";
	}

	public abstract boolean isCriteria();

	public abstract String getJavaType();

	public abstract boolean isCollection();

	public abstract String getSerialVersion();

	public abstract String getImplName(int scope);

	public abstract String getImplName();

	public abstract String getImplFullName(int scope);

	public abstract String getFullName(int scope);

	public abstract String getImplFullName();

	public abstract String getFullName();

	public abstract String getPackagePrefix();

	public abstract AbstractModelClass getSuperClass();

	public abstract String getComments();

	public abstract String getName();

	public abstract String getPackage();

	public abstract String getFile();

	public abstract String getPackageDir(int scope);

	public abstract List<AbstractModelClass> getDepends();

	public abstract List<AbstractModelAttribute> getAttributes();

	public abstract List<ModelOperation> getOperations();

	public abstract boolean isIndexUnique();

	public abstract String getIndexName();

	public abstract AbstractModelClass getIndexEntity();

	public abstract String[] getIndexColumns();

	public abstract boolean isIndex();

	public abstract boolean isRole();

	public abstract boolean isEnumeration();

	public abstract boolean isValueObject();

	public abstract boolean isService();

	public abstract boolean isEntity();

	public abstract String getId();

	public abstract AbstractModelClass getChildClass();

	public abstract boolean isArray();

	public abstract boolean isGenerated();

	public abstract LinkedList<AbstractModelClass> getSpecializations();

	public abstract LinkedList<AbstractModelClass> getProvides();

	public abstract LinkedList<AbstractModelAttribute> getForeignKeys();

	public abstract boolean hasTenantAttribute ();

	public String generatePlantUml(AbstractModelClass fromClass, int scope, boolean attributes, boolean methods) {
		return generatePlantUml(fromClass, scope, attributes, methods, "", false);
	}

	public String generatePlantUml(AbstractModelClass fromPath, int scope, boolean attributes, boolean methods,
			String extraAttributes, boolean columns) {
				String relative = "";
				if (isService() || isEntity() || isRole() || isValueObject())
					relative = " [["+generateRef (fromPath, this, Translate.DEFAULT)+"]]";
				
				StringBuffer b = new StringBuffer();
				if (isRole())
				{
					b.append("actor "+getName(scope )+relative);
				}
				else if (isEntity ())
				{
					b.append ("class "+
							(columns ? getTableName() : getName(scope))+" <<(E,#ff4040) Entity>>"+relative);
				} 
				else if (isService())
				{
					b.append ("class "+
							getName(scope)+" <<(S,#ffff00) Service>>"+relative);
				}
				else if (isEnumeration())
				{
					b.append ("class "+getName(scope)+" <<(E,#ffff00) Enumeration>>"+relative);
				}
				else if (isValueObject())
				{
					b.append ("class "+getName(scope)+" <<(V,#4040ff) ValueObject>>"+relative);
				}
				b.append(extraAttributes);
				if (isGenerated() && (attributes || methods))
				{			
					b.append ("{\n");
					if (attributes)
						for (AbstractModelAttribute attribute: getAttributes())
						{
							if (columns && isEntity())
							{
								if (attribute.getColumn() != null && attribute.getColumn().trim().length()> 0)
								{
									b.append ("  ")
									.append (attribute.getColumn())
									.append(" ")
									.append (attribute.getDdlType(scope));
								if (!attribute.isRequired())
									b.append (" \"0..1\"");
								b.append ("\n");
								}
							}
							else
							{
								b.append ("  ")
									.append (attribute.getName(scope))
									.append(":")
									.append (attribute.getDataType().getJavaType(Translate.DEFAULT));
								if (!attribute.isRequired())
									b.append (" \"0..1\"");
								b.append ("\n");
							}
						}
					if (methods)
						for (ModelOperation operation: getOperations())
						{
							b.append ("  ")
							.append (operation.getName(scope))
							.append("(");
							boolean first = true;
							for (ModelParameter p: operation.getParameters())
							{
								if (!first) b.append(",");
								first = false;
								b.append (p.getName(scope));
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

	private String generateRef(AbstractModelClass fromPath,
			AbstractModelClass abstractModelClass, int scope) {
		return generateRef (getDocFile(fromPath, scope), getDocFile(this, scope));
	}


	private String generateRef(String docFile, String docFile2) {
		StringBuffer ref = new StringBuffer();
		int lastSlash = docFile.length();
		do
		{
			lastSlash = docFile.lastIndexOf(File.separatorChar, lastSlash-1);
			if (lastSlash < 0)
				return docFile2;
			if (lastSlash < docFile2.length() && 
					docFile2.substring(0, lastSlash).equals(docFile.substring(0, lastSlash)))
				break;
			ref.append("../");
		} while (true);
		ref.append (docFile2.substring(lastSlash+1).replace(File.separatorChar, '/'));
		return ref.toString();
	}


	private String getDocFile(
			AbstractModelClass abstractModelClass, int scope) {
		return "."+File.separator+Util.packageToDir(abstractModelClass.getPackage(scope))+
				abstractModelClass.getName(scope)+".html";
	}


	public AbstractModelAttribute searchForeignKey(AbstractModelClass foreignClass, AbstractModelAttribute foreignAttribute) {
		if (foreignClass == null || foreignAttribute == null)
			return null;
		String column = foreignAttribute.getForeignKey();
		
		if (column == null)
			return null;
		
		for (AbstractModelAttribute att2: getAttributes())
		{
			if (column.equals(att2.getColumn()))
			{
				return att2;
			}
		}
		return null;
	}

	public AbstractModelAttribute searchReverseForeignKey(AbstractModelClass foreignClass, AbstractModelAttribute foreignAttribute) {
		if (foreignClass == null || foreignAttribute == null)
			return null;
		String column = foreignAttribute.getColumn();
	
		if (column == null)
			return null;
		
		for (AbstractModelAttribute att2: getAttributes())
		{
			if (column.equals(att2.getForeignKey()))
			{
				return att2;
			}
		}
		return null;
	}


	public boolean isStateful() {
		return false;
	}


	public boolean isConsoleOnly() {
		return false;
	}


	public String getDescription() {
		return null;
	}


	public abstract boolean isJsonObject() ;

	public abstract JsonObject getJsonObject () ;

	public int getCache() {
		return 0;
	}

}
