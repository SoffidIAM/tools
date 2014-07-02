package com.soffid.mda.parser;

import com.soffid.mda.generator.Util;

public abstract class AbstractModelAttribute extends ModelElement {

	public AbstractModelAttribute(Parser parser) {
		super(parser);
	}

	public abstract AbstractModelAttribute getReverseAttribute ();
	
	public abstract String getJavaType(boolean translated, boolean translated2);

	public abstract String getConstantValue();

	public abstract String getDdlType(boolean translated);

	public abstract AbstractModelClass getModelClass();

	public abstract String getForeignKey();

	public abstract String getHibernateType(boolean translated);

	public abstract boolean isRequired();

	public abstract String getCriteriaComparator();

	public abstract String getCriteriaParameter();

	public abstract boolean isIdentifier();

	public abstract String getColumn();

	public abstract int getIntegerLength();

	public abstract String getLength();

	public abstract String getDefaultValue();

	public abstract AbstractModelClass getDataType();

	public abstract String getName(boolean translated);

	public abstract String getComments();

	public abstract String getId();

	public String getName() {
		return getName(false);
	}

	public String getterName(boolean translated) {
		if (getDataType().getJavaType().equals("boolean"))
			return "is"+Util.firstUpper(getName(translated));
		else
			return "get"+Util.firstUpper(getName(translated));
	}

	public String setterName(boolean translate) {
		return "set"+Util.firstUpper(getName(translate));
	}

	public String getJavaType(boolean translated) {
		return getDataType().getJavaType(translated);
	}

	public abstract boolean isCascadeDelete ();
	public abstract boolean isCascadeNullify();

	public boolean isComposition() {
		return false;
	}
}