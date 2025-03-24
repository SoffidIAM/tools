package com.soffid.mda.parser;

import java.lang.reflect.Field;

import com.soffid.mda.annotation.Attribute;

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
	public String getConstantValue() {
		return null;
	}

	@Override
	public String getDdlType(int scope) {
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
	public String getHibernateType(int scope) {
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
	public String getName(int scope) {
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

	@Override
	public String getEntityAttribute() {
		return null;
	}

	@Override
	public String getEntityJoin() {
		return null;
	}

	@Override
	public String getJsonName() {
		return null;
	}

	@Override
	public String getJsonHibernateAttribute() {
		return null;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public Object getStaticValue() {
		return null;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public String[] getSynonyms() {
		return new String[0];
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public String getUiType() {
		return null;
	}

	@Override
	public String getCase() {
		return null;
	}

	@Override
	public String getFilterExpression() {
		return null;
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

	@Override
	public String getSeparator() {
		return null;
	}

	@Override
	public String getValidator() {
		return null;
	}

	@Override
	public boolean isMultiline() {
		return false;
	}

	@Override
	public boolean isSearchCriteria() {
		return false;
	}

	@Override
	public String getCustomUiHandler() {
		return null;
	}

	@Override
	public boolean isMultivalue() {
		return true;
	}

	@Override
	public String[] getListOfValues() {
		return null;
	}

	@Override
	public String getLabel() {
		return null;
	}
}
