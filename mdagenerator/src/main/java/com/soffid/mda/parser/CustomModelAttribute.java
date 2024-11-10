package com.soffid.mda.parser;

import java.lang.reflect.Field;

import com.soffid.mda.annotation.Attribute;

public class CustomModelAttribute extends AbstractModelAttribute {
	AbstractModelClass ownerClass;
	AbstractModelClass dataType;
	String name;
	String column;
	
	public void setColumn(String column) {
		this.column = column;
	}

	public CustomModelAttribute(Parser parser, AbstractModelClass ownerClass, Class cl, String name, String columnName) {
		super(parser);
		this.name = name;
		this.ownerClass = ownerClass;
		this.dataType = (AbstractModelClass) parser.getElement(cl);
		this.name = name;
		this.column = columnName;
	}

	@Override
	public String getConstantValue() {
		return null;
	}

	@Override
	public String getDdlType(int scope) {
		String t = getJavaType(scope);
		if (getDataType().getName().equals ("Blob") || t.equals("byte[]"))
			return 	"org.springframework.orm.hibernate3.support.BlobByteArrayType";
		if (t.equals( "Long") )
			return "java.lang.Long";
		if (t.equals("String"))
			return "java.lang.String";
		if (t.equals("Integer"))
			return"java.lang.Integer";
		if (t .equals("Boolean"))
			return "java.lang.Boolean";
		if (getDataType().isEnumeration())
			return t + "Enum";
		return t;
	}

	@Override
	public AbstractModelClass getModelClass() {
		return ownerClass;
	}

	@Override
	public String getForeignKey() {
		return null;
	}

	@Override
	public String getHibernateType(int scope) {
		return dataType.getName(scope);
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
		return column;
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
}
