package com.soffid.mda.parser;

import java.lang.reflect.Field;

import javax.swing.text.StyledEditorKit.UnderlineAction;

import com.soffid.mda.annotation.Attribute;
import com.soffid.mda.annotation.Column;
import com.soffid.mda.annotation.CriteriaColumn;
import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.ForeignKey;
import com.soffid.mda.annotation.Identifier;
import com.soffid.mda.annotation.Nullable;
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
	public String getName(boolean translated) {
		if (translated)
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
				return null;
			else
				return annotation.name();
		}
		else
			return null;
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
	public String getHibernateType(boolean translated) {
		String t = getJavaType(translated);
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
	public String getDdlType(boolean translated) {
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
			javaType = sample.getJavaType(translated);
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
	public String getJavaType(boolean translated, boolean translated2) {
		return getDataType().getJavaType(translated, translated2);
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
}
