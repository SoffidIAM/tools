package com.soffid.mda.parser;

import java.lang.reflect.Field;

public class ReverseModelAttribute extends AbstractModelAttribute {

	AbstractModelClass dataType;
	private AbstractModelAttribute attribute;
	private AbstractModelClass ownerClass;
	String name;
	
	public ReverseModelAttribute(Parser parser, AbstractModelAttribute originalAttribute, String name) {
		super(parser);
		this.attribute = originalAttribute;
		dataType = new ModelClassCollection(parser, originalAttribute.getModelClass());
		ownerClass = originalAttribute.getDataType();
		this.name = name;
	}

	@Override
	public String getJavaType(boolean translated, boolean translatedOnly) {
		return dataType.getJavaType(translated, translatedOnly);
	}

	@Override
	public String getConstantValue() {
		return null;
	}

	@Override
	public String getDdlType(boolean translated) {
		return null;
	}

	@Override
	public AbstractModelClass getModelClass() {
		return ownerClass;
	}

	@Override
	public String getForeignKey() {
		return attribute.getColumn();
	}

	@Override
	public String getHibernateType(boolean translated) {
		return null;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getCriteriaComparator() {
		return null;
	}

	@Override
	public String getCriteriaParameter() {
		return null;
	}

	@Override
	public boolean isIdentifier() {
		return false;
	}

	@Override
	public String getColumn() {
		return null;
	}

	@Override
	public int getIntegerLength() {
		return 0;
	}

	@Override
	public String getLength() {
		return null;
	}

	@Override
	public String getDefaultValue() {
		return null;
	}

	@Override
	public AbstractModelClass getDataType() {
		return dataType;
	}

	@Override
	public String getName(boolean translated) {
		return name;
	}

	@Override
	public String getComments() {
		return "";
	}

	@Override
	public String getId() {
		return ownerClass.getFullName()+"."+name;
	}

	@Override
	public AbstractModelAttribute getReverseAttribute() {
		return null;
	}

	@Override
	public boolean isCascadeDelete() {
		return false;
	}

	@Override
	public boolean isCascadeNullify() {
		return false;
	}

}
