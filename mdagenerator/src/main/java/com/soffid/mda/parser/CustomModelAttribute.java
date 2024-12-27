package com.soffid.mda.parser;

import java.lang.reflect.Field;

import com.soffid.mda.annotation.Attribute;

public class CustomModelAttribute extends AbstractModelAttribute {
	AbstractModelClass ownerClass;
	AbstractModelClass dataType;
	String name;
	String column;
	private boolean hidden;
	
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
		if (getDataType() == null)
		{
			return "";
		}

		String javaType = getDataType().getJavaType();
		int length = getIntegerLength();
		AbstractModelClass dataType = getDataType();
		
		if (getDataType().isEntity())
		{
			AbstractModelClass foreign = getDataType();
			AbstractModelAttribute foreignKey = foreign.getIdentifier();
			javaType = foreignKey.getDataType().getJavaType();
			length = foreignKey.getIntegerLength();
		}
		else if (getDataType().isEnumeration())
		{
			AbstractModelClass enumeration = getDataType();
			AbstractModelAttribute sample = enumeration.getAttributes().get(0);
			javaType = sample.getJavaType(scope);
			if (length <= 0)
				length = sample.getIntegerLength();
		}

	
		if (dataType.getName().equals("Blob") || javaType.equals("byte[]"))
		{
			String t = "type='BLOB'";
			if (length > 0)
			{
				t += " length='";
				t += length;
				t += "'";
			}
			return t;
		}
		else if (dataType.getName().equals("Clob") || 
				(javaType.equals("java.lang.String") && length > 2000))
		{
			String t = "type='CLOB'";
			if (length > 0)
			{
				t += " length='";
				t += length;
				t += "'";
			}
			return t;
		}
		else if (javaType.equals("java.lang.String"))
		{
			if (length <= 0)
			{
				System.out.println ( "Warning: Attribute  " + getModelClass().getFullName()+"."+getName()
					+ "." + getName() + " is missing length" );
				length = 256;
			}
			return "type='VARCHAR' length='" + length + "'";
		}
		else if (javaType.equals ( "java.lang.Long") || javaType.equals("long"))
		{
			return "type='BIGINT'" ;
		}
		else if (javaType.equals ( "java.lang.Integer") || javaType.equals("int"))
		{
			return "type='INTEGER'" ;
		}
		else if (javaType.equals ( "java.lang.Double") || javaType.equals("double")
			|| javaType.equals ( "java.lang.Float") || javaType.equals("float"))
		{
			return "type='FLOAT'" ;
		}
		else if (javaType.equals ( "boolean" ) || javaType.equals ( "java.lang.Boolean"))
		{
			return  "type='BIT'";
		}
		else if (javaType.equals ( "java.util.Date" )|| javaType.equals( "java.sql.Timestamp") || javaType.equals( "java.sql.DateTime"))
		{
			return "type='DATE'" ;
		}
		else
		{
			return "";
		}
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
		return dataType.getFullName(scope);
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
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String getUiType() {
		String javaType = getDataType().getJavaType();
		if (javaType.endsWith("Date"))
			return "DATE_TIME";
		else
			return "USER";
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
		return true;
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
		return false;
	}

	@Override
	public String[] getListOfValues() {
		return null;
	}
}
