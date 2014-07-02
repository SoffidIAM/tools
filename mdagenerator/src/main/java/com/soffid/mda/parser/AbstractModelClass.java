package com.soffid.mda.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.soffid.mda.generator.DocGenerator;
import com.soffid.mda.generator.Generator;
import com.soffid.mda.generator.Util;

public abstract class AbstractModelClass extends ModelElement {
	public boolean left;

	public AbstractModelClass(Parser parser) {
		super(parser);
	}


	public abstract String getJavaType(boolean translated, boolean translatedOnly);

	public abstract String getFile(boolean translated);

	public abstract long lastModified();

	public abstract boolean isException();

	public abstract String getXmlId();

	public abstract List<AbstractModelAttribute> getAllAttributes();

	public abstract String getBaseFullName(boolean translated);

	public abstract String getBaseName(boolean translated);

	public abstract String getLocalServiceName(boolean translated);

	public abstract String getRawType();

	public abstract String getRoleName();

	public abstract String getBeanFullName(boolean translated);

	public abstract String getEjbName(boolean translated);

	public abstract String getSpringBeanName(Generator generator, boolean translated);

	public abstract boolean isTranslated();

	public abstract String getEjbHomeFullName(boolean translated);

	public abstract String getEjbPackage(boolean translated);

	public abstract String getEjbInterfaceFullName(boolean translated);

	public abstract String getBeanName(boolean translated);

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

	public abstract String getDaoImplFullName(boolean translated);

	public abstract boolean isVoid();

	public abstract boolean isString();

	public abstract boolean isPrimitive();

	public abstract String getVarName();

	public abstract String getDaoBaseName(boolean translated);

	public abstract String getDaoImplName(boolean translated);

	public abstract boolean hasNonStaticMethods();

	public abstract String getDaoFullName(boolean translated);

	public abstract String getDaoName(boolean translated);

	public abstract AbstractModelAttribute getIdentifier();

	public abstract void fixup();

	public abstract void setGenerated(boolean b);

	public abstract String getPackage(boolean translated);

	public abstract String getPackagePrefix(boolean translated);

	public abstract String getName(boolean translated);

	public abstract String getJavaType(boolean translated);

	public abstract boolean isCriteria();

	public abstract String getJavaType();

	public abstract boolean isCollection();

	public abstract String getSerialVersion();

	public abstract String getImplName(boolean translated);

	public abstract String getImplName();

	public abstract String getImplFullName(boolean translated);

	public abstract String getFullName(boolean translated);

	public abstract String getImplFullName();

	public abstract String getFullName();

	public abstract String getPackagePrefix();

	public abstract AbstractModelClass getSuperClass();

	public abstract String getComments();

	public abstract String getName();

	public abstract String getPackage();

	public abstract String getFile();

	public abstract String getPackageDir(boolean translated);

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

	public String generatePlantUml(AbstractModelClass fromClass, boolean translated, boolean attributes, boolean methods) {
		return generatePlantUml(fromClass, translated, attributes, methods, "");
	}

	public String generatePlantUml(AbstractModelClass fromPath, boolean translated, boolean attributes, boolean methods,
			String extraAttributes) {
				String relative = "";
				if (isService() || isEntity() || isRole() || isValueObject())
					relative = " [["+generateRef (fromPath, this, false)+"]]";
				
				StringBuffer b = new StringBuffer();
				if (isRole())
				{
					b.append("actor "+getName(translated)+relative);
				}
				else if (isEntity ())
				{
					b.append ("class "+
							getName(translated)+" <<(E,#ff4040) Entity>>"+relative);
				} 
				else if (isService())
				{
					b.append ("class "+
							getName(translated)+" <<(S,#ffff00) Service>>"+relative);
				}
				else if (isEnumeration())
				{
					b.append ("class "+getName(translated)+" <<(E,#ffff00) Enumeration>>"+relative);
				}
				else if (isValueObject())
				{
					b.append ("class "+getName(translated)+" <<(V,#4040ff) ValueObject>>"+relative);
				}
				b.append(extraAttributes);
				if (isGenerated() && (attributes || methods))
				{			
					b.append ("{\n");
					if (attributes)
						for (AbstractModelAttribute attribute: getAttributes())
						{
							b.append ("  ")
								.append (attribute.getName(translated))
								.append(":")
								.append (attribute.getDataType().getJavaType(true));
							if (!attribute.isRequired())
								b.append (" \"0..1\"");
/*							if (attribute.getDataType().isGenerated() &&
									attribute.getDataType().isEntity() ||
									attribute.getDataType().isService() ||
									attribute.getDataType().isValueObject())
								b.append (" [["+generateRef(fromPath, this, translated)+"]]");
*/							b.append ("\n");
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

	private String generateRef(AbstractModelClass fromPath,
			AbstractModelClass abstractModelClass, boolean translated) {
		return generateRef (getDocFile(fromPath, translated), getDocFile(this, translated));
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
			AbstractModelClass abstractModelClass, boolean translated) {
		return "."+File.separator+Util.packageToDir(abstractModelClass.getPackage(translated))+abstractModelClass.getName(translated)+".html";
	}


	public AbstractModelAttribute searchForeignKey(AbstractModelClass foreignClass, AbstractModelAttribute foreignAttribute) {
		if (foreignClass == null || foreignAttribute == null)
			return null;
		String column = foreignAttribute.getForeignKey();
	
		
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

}