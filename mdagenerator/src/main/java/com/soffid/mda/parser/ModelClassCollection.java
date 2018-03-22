package com.soffid.mda.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.soffid.mda.annotation.JsonObject;
import com.soffid.mda.generator.Generator;
import com.soffid.mda.generator.Translate;

public class ModelClassCollection extends AbstractModelClass {
	
	
	private AbstractModelClass modelClass;

	public ModelClassCollection(Parser parser, AbstractModelClass modelClass) {
		super(parser);
		this.modelClass = modelClass;
	}

	@Override
	public String getJavaType(int scope) {
		return "java.util.Collection<"+modelClass.getJavaType(scope)+">";
	}

	@Override
	public String getFile(int scope) {
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
	public String getBaseFullName(int scope) {
		return "";
	}

	@Override
	public String getBaseName(int scope) {
		return "";
	}

	@Override
	public String getLocalServiceName(int scope) {
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
	public String getBeanFullName(int scope) {
		return "";
	}

	@Override
	public String getEjbName(int scope) {
		return "";
	}

	@Override
	public String getSpringBeanName(Generator generator, int scope) {
		return "";
	}

	@Override
	public boolean isTranslated() {
		return false;
	}

	@Override
	public String getEjbHomeFullName(int scope) {
		return "";
	}

	@Override
	public String getEjbPackage(int scope) {
		return "";
	}

	@Override
	public String getEjbInterfaceFullName(int scope) {
		return "";
	}

	@Override
	public String getBeanName(int scope) {
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
	public String getDaoImplFullName(int scope) {
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
	public String getDaoBaseName(int scope) {
		return "";
	}

	@Override
	public String getDaoImplName(int scope) {
		return "";
	}

	@Override
	public boolean hasNonStaticMethods() {
		return false;
	}

	@Override
	public String getDaoFullName(int scope) {
		return "";
	}

	@Override
	public String getDaoName(int scope) {
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
	public String getPackage(int scope) {
		return "java.util";
	}

	@Override
	public String getPackagePrefix(int scope) {
		return "java.util.";
	}

	@Override
	public String getName(int scope) {
		return getJavaType(scope);
	}

	@Override
	public boolean isCriteria() {
		return false;
	}

	@Override
	public String getJavaType() {
		return "java.util.Collection<"+modelClass.getJavaType()+">";
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
	public String getImplName(int scope) {
		return "";
	}

	@Override
	public String getImplName() {
		return "";
	}

	@Override
	public String getImplFullName(int scope) {
		return "";
	}

	@Override
	public String getFullName(int scope) {
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
		return getPackagePrefix(Translate.DEFAULT);
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
		return getName(Translate.DEFAULT);
	}

	@Override
	public String getPackage() {
		return getPackage(Translate.DEFAULT);
	}

	@Override
	public String getFile() {
		return null;
	}

	@Override
	public String getPackageDir(int scope) {
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

	@Override
	public JsonObject getJsonObject() {
		return null;
	}

	@Override
	public boolean isTranslatedImpl() {
		return false;
	}

	@Override
	public boolean hasTenantAttribute() {
		return false;
	}

	@Override
	public boolean isFuture() {
		return false;
	}

}
