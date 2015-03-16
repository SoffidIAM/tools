package com.soffid.mda.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.soffid.mda.annotation.JsonObject;
import com.soffid.mda.generator.Generator;

public class ModelClassCollection extends AbstractModelClass {
	
	
	private AbstractModelClass modelClass;

	public ModelClassCollection(Parser parser, AbstractModelClass modelClass) {
		super(parser);
		this.modelClass = modelClass;
	}

	@Override
	public String getJavaType(boolean translated, boolean translatedOnly) {
		return "java.util.Collection<"+modelClass.getJavaType(translated, translatedOnly)+">";
	}

	@Override
	public String getFile(boolean translated) {
		return modelClass.getFile();
	}

	@Override
	public long lastModified() {
		return modelClass.lastModified();
	}

	@Override
	public boolean isException() {
		return false;
	}

	@Override
	public String getXmlId() {
		return "collection."+modelClass.getXmlId();
	}

	@Override
	public List<AbstractModelAttribute> getAllAttributes() {
		return new LinkedList<AbstractModelAttribute>();
	}

	@Override
	public String getBaseFullName(boolean translated) {
		return "";
	}

	@Override
	public String getBaseName(boolean translated) {
		return "";
	}

	@Override
	public String getLocalServiceName(boolean translated) {
		return "";
	}

	@Override
	public String getRawType() {
		return "java.util.Collection";
	}

	@Override
	public String getRoleName() {
		return "";
	}

	@Override
	public String getBeanFullName(boolean translated) {
		return "";
	}

	@Override
	public String getEjbName(boolean translated) {
		return "";
	}

	@Override
	public String getSpringBeanName(Generator generator, boolean translated) {
		return "";
	}

	@Override
	public boolean isTranslated() {
		return false;
	}

	@Override
	public String getEjbHomeFullName(boolean translated) {
		return "";
	}

	@Override
	public String getEjbPackage(boolean translated) {
		return "";
	}

	@Override
	public String getEjbInterfaceFullName(boolean translated) {
		return "";
	}

	@Override
	public String getBeanName(boolean translated) {
		return "";
	}

	@Override
	public List<AbstractModelClass> getActors() {
		return Collections.emptyList();
	}

	@Override
	public Set<AbstractModelClass> getAllActors() {
		return Collections.emptySet();
	}

	@Override
	public String getServerRole() {
		return "";
	}

	@Override
	public String getServerPath() {
		return "";
	}

	@Override
	public boolean isServerOnly() {
		return false;
	}

	@Override
	public boolean isInternal() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public String getDiscriminatorColumn() {
		return "";
	}

	@Override
	public String getDiscriminatorValue() {
		return "";
	}

	@Override
	public String getTableName() {
		return "";
	}

	@Override
	public String getDaoImplFullName(boolean translated) {
		return "";
	}

	@Override
	public boolean isVoid() {
		return false;
	}

	@Override
	public boolean isString() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public String getVarName() {
		return "";
	}

	@Override
	public String getDaoBaseName(boolean translated) {
		return "";
	}

	@Override
	public String getDaoImplName(boolean translated) {
		return "";
	}

	@Override
	public boolean hasNonStaticMethods() {
		return false;
	}

	@Override
	public String getDaoFullName(boolean translated) {
		return "";
	}

	@Override
	public String getDaoName(boolean translated) {
		return "";
	}

	@Override
	public AbstractModelAttribute getIdentifier() {
		return null;
	}

	@Override
	public void fixup() {
	}

	@Override
	public void setGenerated(boolean b) {
	}

	@Override
	public String getPackage(boolean translated) {
		return "java.util";
	}

	@Override
	public String getPackagePrefix(boolean translated) {
		return "java.util.";
	}

	@Override
	public String getName(boolean translated) {
		return getJavaType(translated);
	}

	@Override
	public String getJavaType(boolean translated) {
		return getJavaType(translated, false);
	}

	@Override
	public boolean isCriteria() {
		return false;
	}

	@Override
	public String getJavaType() {
		return getJavaType(false);
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public String getSerialVersion() {
		return "";
	}

	@Override
	public String getImplName(boolean translated) {
		return "";
	}

	@Override
	public String getImplName() {
		return "";
	}

	@Override
	public String getImplFullName(boolean translated) {
		return "";
	}

	@Override
	public String getFullName(boolean translated) {
		return "";
	}

	@Override
	public String getImplFullName() {
		return "";
	}

	@Override
	public String getFullName() {
		return "";
	}

	@Override
	public String getPackagePrefix() {
		return getPackagePrefix(false);
	}

	@Override
	public AbstractModelClass getSuperClass() {
		return null;
	}

	@Override
	public String getComments() {
		return "";
	}

	@Override
	public String getName() {
		return getName(false);
	}

	@Override
	public String getPackage() {
		return getPackage(false);
	}

	@Override
	public String getFile() {
		return null;
	}

	@Override
	public String getPackageDir(boolean translated) {
		return "";
	}

	@Override
	public List<AbstractModelClass> getDepends() {
		return new LinkedList<AbstractModelClass>();
	}

	@Override
	public List<AbstractModelAttribute> getAttributes() {
		return new LinkedList<AbstractModelAttribute>();
	}

	@Override
	public List<ModelOperation> getOperations() {
		return new LinkedList<ModelOperation>();
	}

	@Override
	public boolean isIndexUnique() {
		return false;
	}

	@Override
	public String getIndexName() {
		return null;
	}

	@Override
	public AbstractModelClass getIndexEntity() {
		return null;
	}

	@Override
	public String[] getIndexColumns() {
		return null;
	}

	@Override
	public boolean isIndex() {
		return false;
	}

	@Override
	public boolean isRole() {
		return false;
	}

	@Override
	public boolean isEnumeration() {
		return false;
	}

	@Override
	public boolean isValueObject() {
		return false;
	}

	@Override
	public boolean isJsonObject ()
	{
		return false;
	}

	@Override
	public boolean isService() {
		return false;
	}

	@Override
	public boolean isEntity() {
		return false;
	}

	@Override
	public String getId() {
		return getJavaType();
	}

	@Override
	public AbstractModelClass getChildClass() {
		return modelClass;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isGenerated() {
		return false;
	}

	@Override
	public LinkedList<AbstractModelClass> getSpecializations() {
		return null;
	}

	@Override
	public LinkedList<AbstractModelClass> getProvides() {
		return null;
	}

	@Override
	public LinkedList<AbstractModelAttribute> getForeignKeys() {
		return null;
	}

}
