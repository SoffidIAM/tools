package com.soffid.tools.db.schema;

import java.util.ArrayList;
import java.util.Vector;

public class ForeignKey extends Index {
	public ForeignKey ()
	{
		super ();
		foreignKeyColumns = new Vector<String>();
		unique = false;
	}
	public String foreignTable;
	public Vector<String> foreignKeyColumns;
}
