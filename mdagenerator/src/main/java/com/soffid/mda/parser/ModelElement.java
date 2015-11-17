package com.soffid.mda.parser;

import com.soffid.mda.annotation.Role;

public abstract class ModelElement {
	protected Parser parser;
	
	public Parser getParser() {
		return parser;
	}
	public ModelElement (Parser parser) 
	{
		this.parser = parser;
	}
	public abstract String getId ();
	
	public abstract String getComments ();
}
