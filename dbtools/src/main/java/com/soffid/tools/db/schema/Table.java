package com.soffid.tools.db.schema;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {
	
	public Table() {
		super();
		columns = new ArrayList<Column>();
	}
	public String name;
	public ArrayList<Column> columns;

	public String getPrimaryKey ()
	{
		String pk = null;
		for (Column c: columns)
		{
			if (c.primaryKey)
			{
				if (pk == null)
					pk = c.name;
				else
					pk = pk + ", " + c.name;
			}
		}
		return pk;
	}
	public Column findColumn(String columnName, boolean caseSensitive) {
		for (Column c: columns)
		{
			if (caseSensitive? columnName.equals(c.name): columnName.equalsIgnoreCase(c.name))
				return c;
		}
		return null;
	}
}
