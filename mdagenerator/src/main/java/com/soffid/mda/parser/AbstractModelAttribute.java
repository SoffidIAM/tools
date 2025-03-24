package com.soffid.mda.parser;

import com.soffid.mda.generator.Translate;
import com.soffid.mda.generator.Util;

public abstract class AbstractModelAttribute extends ModelElement {

	public AbstractModelAttribute(Parser parser) {
		super(parser);
	}

	public abstract boolean isTransient();
	
	public abstract AbstractModelAttribute getReverseAttribute ();
	
	public abstract String getConstantValue();

	public abstract String getDdlType(int scope);

	public abstract AbstractModelClass getModelClass();

	public abstract String getForeignKey();

	public abstract String getHibernateType(int scope);

	public abstract boolean isRequired();

	public abstract String getCriteriaComparator();

	public abstract String getCriteriaParameter();

	public abstract boolean isIdentifier();

	public abstract String getColumn();
	
	public abstract String[] getSynonyms();

	public abstract int getIntegerLength();

	public abstract String getLength();

	public abstract String getDefaultValue();

	public abstract AbstractModelClass getDataType();

	public abstract String getName(int scope);

	public abstract String getComments();

	public abstract String getId();

	public String getName() {
		return getName(Translate.DEFAULT);
	}

	public String getterName(int scope) {
		if (getDataType().getJavaType().equals("boolean"))
			return "is"+Util.firstUpper(getName(scope));
		else
			return "get"+Util.firstUpper(getName(scope));
	}

	public String setterName(int scope) {
		return "set"+Util.firstUpper(getName(scope));
	}

	public String getJavaType(int scope) {
		return getDataType().getJavaType(scope);
	}

	public abstract boolean isCascadeDelete ();
	public abstract boolean isCascadeNullify();

	public boolean isComposition() {
		return false;
	}

	public abstract String getEntityAttribute() ;
	
	public abstract String getEntityJoin();

	@Deprecated
	public abstract String getJsonName();

	public abstract String getJsonHibernateAttribute() ;
	
	public abstract boolean isStatic ();
	
	public abstract Object getStaticValue();

	public String setterName(String synonym) {
		return "set"+Util.firstUpper(synonym);
	}

	public String getterName(String synonym) {
		if (getDataType().getJavaType().equals("boolean"))
			return "is"+Util.firstUpper(synonym);
		else
			return "get"+Util.firstUpper(synonym);
	}

	public abstract boolean isHidden() ;

	public abstract String getUiType();

	public abstract String getCase();

	public abstract String getFilterExpression();

	public abstract boolean isReadonly();

	public abstract String getSeparator();

	public abstract String getValidator();

	public abstract boolean isMultiline();

	public abstract boolean isSearchCriteria();

	public abstract String getCustomUiHandler();

	public abstract boolean isMultivalue();

	public abstract String[] getListOfValues();

	public abstract String getLabel();

}