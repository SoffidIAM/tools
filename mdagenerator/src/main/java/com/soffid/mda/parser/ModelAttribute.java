package com.soffid.mda.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.soffid.mda.annotation.Attribute;
import com.soffid.mda.annotation.Column;
import com.soffid.mda.annotation.CriteriaColumn;
import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.ForeignKey;
import com.soffid.mda.annotation.Identifier;
import com.soffid.mda.annotation.JsonAttribute;
import com.soffid.mda.annotation.Nullable;
import com.soffid.mda.generator.Translate;
import com.soffid.mda.generator.Util;

public class ModelAttribute extends AbstractModelAttribute {
	private Field field;

	public ModelAttribute (Parser parser, Field m)
	{
		super (parser);
		field = m;
	}

	@Override
	public String getId() {
		return field.toString();
	}

	@Override
	public String getComments() {
		Description annotation = (Description) field.getAnnotation (Description.class);
		if (annotation != null)
		{
			if (annotation.value() == null || annotation.value().length() == 0)
				return null;
			else
				return annotation.value();
		}
		else
			return null;
	}

	@Override
	public String getName(int scope) {
		if (Translate.mustTranslate(getModelClass(), scope))
		{
			Attribute annotation = (Attribute) field.getAnnotation (Attribute.class);
			if (annotation != null)
			{
				if (annotation.translated() != null && annotation.translated().length() > 0)
					return annotation.translated();
			}
			ForeignKey fk = (ForeignKey) field.getAnnotation (ForeignKey.class);
			if (fk != null)
			{
				if (fk.translated() != null && fk.translated().length() > 0)
					return fk.translated();
			}
			Column cl = (Column) field.getAnnotation (Column.class);
			if (cl != null)
			{
				if (cl.translated() != null && cl.translated().length() > 0)
					return cl.translated();
			}
		}
		return field.getName();
	}

	@Override
	public ModelClass getDataType() {
		return (ModelClass) parser.getElement(field.getGenericType());
	}

	@Override
	public String getDefaultValue() {
		Column annotation = (Column) field.getAnnotation (Column.class);
		if (annotation != null)
		{
			if (annotation.defaultValue() == null || annotation.defaultValue().length() == 0)
				return null;
			else
				return annotation.defaultValue();
		}
		Attribute annotation2 = (Attribute) field.getAnnotation (Attribute.class);
		if (annotation2 != null)
		{
			if (annotation2.defaultValue() == null || annotation2.defaultValue().length() == 0)
				return null;
			else
				return annotation2.defaultValue();
		}
		else
			return null;
	}

	@Override
	public String getLength() {
		Column annotation = (Column) field.getAnnotation (Column.class);
		if (annotation != null)
		{
			if (annotation.length() <= 0)
				return null;
			else
				return ""+annotation.length();
		}
		else
			return null;
	}

	@Override
	public int getIntegerLength() {
		Column annotation = (Column) field.getAnnotation (Column.class);
		if (annotation != null)
		{
			if (annotation.length() <= 0)
				return -1;
			else
				return annotation.length();
		}
		else
			return -1;
	}

	
	@Override
	public String getColumn() {
		Column annotation = (Column) field.getAnnotation (Column.class);
		if (annotation != null)
		{
			if (annotation.name() == null || annotation.name().length() == 0)
				return getName();
			else
				return annotation.name();
		}
		else
			return getName();
	}

	@Override
	public boolean isIdentifier() {
		return field.getAnnotation(Identifier.class) != null;
	}

	@Override
	public String getCriteriaParameter() {
		CriteriaColumn annotation = (CriteriaColumn) field.getAnnotation (CriteriaColumn.class);
		if (annotation != null)
		{
			if (annotation.parameter() != null || annotation.parameter().length() > 0)
				return annotation.parameter();
		}
		return null;
	}

	@Override
	public String getCriteriaComparator() {
		CriteriaColumn annotation = (CriteriaColumn) field.getAnnotation (CriteriaColumn.class);
		if (annotation != null)
		{
			if (annotation.comparator() != null || annotation.comparator().length() > 0)
				return annotation.comparator();
		}
		return null;
	}

	@Override
	public boolean isRequired() {
		return field.getAnnotation(Nullable.class) == null;
	}

	@Override
	public String getHibernateType(int scope) {
		String t = getJavaType(scope);
		if (getDataType().getName().equals ("Blob"))
			return 	"java.sql.Blob";
		else if (t.equals("byte[]"))
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
	public String getForeignKey ()
	{
		ForeignKey fk = field.getAnnotation(ForeignKey.class);
		if (fk != null)
			return fk.foreignColumn();
		else
			return null;
	}

	@Override
	public ModelClass getModelClass() {
		return (ModelClass) parser.getElement(field.getDeclaringClass());
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
	public String getConstantValue() {
		Object obj;
		try {
			obj = field.getDeclaringClass().newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException (e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException (e);
		}
		Object value;
		try {
			value = field.get(obj);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException (e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException (e);
		}
		if (value == null)
			return "null";
		else if (value instanceof Integer ||
				value instanceof Long ||
				value instanceof Boolean ||
				value instanceof Double ||
				value instanceof Float)
			return value.toString();
		else if (value instanceof String)
			return "\""+Util.formatString((String) value)+"\"";
		else
			throw new RuntimeException("Unsupported constant value of class "+value.getClass().getName());
	}

	@Override
	public AbstractModelAttribute getReverseAttribute() {
		Column c = field.getAnnotation(Column.class);
		if (c != null && c.reverseAttribute().length() > 0)
		{
			ReverseModelAttribute ra = new ReverseModelAttribute(parser, this, c.reverseAttribute());
			ReverseModelAttribute ra2 = (ReverseModelAttribute) parser.getElement(ra.getId());
			if (ra2 == null)
			{
				parser.register(ra.getId(), ra);
				return ra;
			}
			else
				return ra2;
		}
		else
			return null;
	}

	@Override
	public boolean isCascadeDelete() {
		Column c = field.getAnnotation(Column.class);
		if (c != null )
			return c.cascadeDelete();
		else
			return false;
	}

	@Override
	public boolean isCascadeNullify() {
		Column c = field.getAnnotation(Column.class);
		if (c != null )
			return c.cascadeNullify();
		else
			return false;
	}

	@Override
	public boolean isComposition() {
		Column c = field.getAnnotation(Column.class);
		if (c != null )
			return c.composition();
		else
			return false;
	}

	@Override
	public String getEntityAttribute() {
		JsonAttribute jsonAttribute = field.getAnnotation(JsonAttribute.class);
		if (jsonAttribute == null)
			return null;
		else
			return jsonAttribute.hibernateAttribute();
	}

	@Override
	public String getEntityJoin() {
		JsonAttribute jsonAttribute = field.getAnnotation(JsonAttribute.class);
		if (jsonAttribute == null)
			return null;
		else
			return jsonAttribute.hibernateJoin();
	}

	@Override
	public String getJsonName() {
		JsonAttribute jsonAttribute = field.getAnnotation(JsonAttribute.class);
		if (jsonAttribute == null || jsonAttribute.value() == null || jsonAttribute.value().length() == 0)
			return field.getName();
		else
			return jsonAttribute.value();
		
	}

	@Override
	public String getJsonHibernateAttribute() {
		JsonAttribute jsonAttribute = field.getAnnotation(JsonAttribute.class);
		if (jsonAttribute == null || jsonAttribute.hibernateAttribute() == null || jsonAttribute.hibernateAttribute().length() == 0)
			return null;
		else
			return jsonAttribute.hibernateAttribute();
		
	}

	@Override
	public boolean isStatic() {
		if ( Modifier.isFinal(field.getModifiers() ) &&
				 Modifier.isStatic(field.getModifiers())) 
				return true;
			else
				return false;
	}

	@Override
	public Object getStaticValue() {
		if (isStatic())
			try {
				return field.get(null);
			} catch (IllegalArgumentException e) {
				return null;
			} catch (IllegalAccessException e) {
				return null;
			}
		else
			return null;
	}

	@Override
	public boolean isTransient() {
		return ( field.getModifiers() & Modifier.TRANSIENT ) != 0;
	}

	@Override
	public String[] getSynonyms() {
		Attribute att = field.getAnnotation(Attribute.class);
		if (att == null || att.synonyms() == null)
			return new String[0];
		else
			return att.synonyms();
	}

	@Override
	public boolean isHidden() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? false: att.hidden();
	}

	@Override
	public String getUiType() {
		Attribute att = field.getAnnotation(Attribute.class);
		if (att != null && att.type() != null && ! att.type().isEmpty())
			return att.type();
		String type = getJavaType(Translate.DEFAULT);
		if ("java.util.Calendar".equals(type) ||
			"java.util.Date".equals(type))
			return "DATE_TIME";
		else if ("java.lang.Boolean".equals(type) ||
				"boolean".equals(type))
				return "BOOLEAN";
		else
			return "STRING";
	}

	@Override
	public String getCase() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null || att.lettercase() == null || att.lettercase().isEmpty()? "MIXEDCASE": att.lettercase();
	}

	@Override
	public String getFilterExpression() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? null : att.filterExpression();
	}

	@Override
	public String getSeparator() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? null : att.separator();
	}

	@Override
	public boolean isReadonly() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? false: att.readonly();
	}

	@Override
	public String getValidator() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? null : att.validatorClass();
	}

	@Override
	public boolean isMultiline() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? false: att.multiline();
	}

	@Override
	public boolean isMultivalue() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? false: att.multivalue();
	}

	@Override
	public boolean isSearchCriteria() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? false: att.searchCriteria();
	}

	@Override
	public String getCustomUiHandler() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? null: att.customUiHandler();
	}

	@Override
	public String[] getListOfValues() {
		Attribute att = field.getAnnotation(Attribute.class);
		return att == null ? null: att.listOfValues();
	}

	@Override
	public String getLabel() {
		Attribute annotation = (Attribute) field.getAnnotation (Attribute.class);
		if (annotation != null)
			return annotation.label();
		else
			return null;
	}

}
