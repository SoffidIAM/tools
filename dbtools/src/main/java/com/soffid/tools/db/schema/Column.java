package com.soffid.tools.db.schema;

import java.io.Serializable;

public class Column implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Column ()
	{
		notNull = false;
		autoIncrement = false;
		primaryKey = false;
	}
	public String name;
	public String type;
	public String length;
	public boolean autoIncrement;
	public boolean notNull;
	public boolean primaryKey;
}
