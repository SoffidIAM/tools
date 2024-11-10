package com.soffid.mda.generator;

import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelClass;

public class Translate {

	final public static int DEFAULT = 0;
	final public static int DONT_TRANSLATE = 1;
	final public static int TRANSLATE = 2;
	final public static int ENTITY_SCOPE = 3;
	final public static int SERVICE_SCOPE = 4;
	final public static int ALTSERVICE_SCOPE = 5;
	final public static int PREVIOUS_SCOPE = 5;
	
	public static boolean mustTranslate(AbstractModelClass modelClass, int scope) {
		if (modelClass.getParser().isTranslateOnly())
			return true;
		if (scope == DONT_TRANSLATE)
			return false;
		else if (scope == TRANSLATE)
			return true;
		else if (scope == PREVIOUS_SCOPE)
		{
			if (modelClass.isEntity())
				return modelClass.getParser().isTranslate();
			else
				return false;
		}
		else if (modelClass.isEntity())
			return modelClass.getParser().isTranslateEntities();
		else if ( scope == SERVICE_SCOPE)
			return modelClass.getParser().isTranslate();
		else if (scope == ALTSERVICE_SCOPE)
			return ! modelClass.getParser().isTranslate();
		else 
			return modelClass.getParser().isTranslate();
	}
	
	public static boolean mustTranslate(int scope, Generator generator)
	{
		if (generator.isTransaltedOnly())
			return true;
		if (scope == DONT_TRANSLATE)
			return false;
		else if (scope == TRANSLATE)
			return true;
		else if ( scope == SERVICE_SCOPE)
			return generator.translated;
		else if (scope == ALTSERVICE_SCOPE)
			return ! generator.translated;
		else 
			return generator.translated;
	}
}
