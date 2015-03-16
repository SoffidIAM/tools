package com.soffid.tools.db.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class Index implements Serializable {
	public Index() {
		super();
		columns = new Vector<String>();
	}
	public String name;
	public String tableName;
	public boolean unique;
	public Vector <String> columns;
}
