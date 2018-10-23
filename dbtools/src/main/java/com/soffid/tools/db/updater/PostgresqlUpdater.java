package com.soffid.tools.db.updater;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import com.soffid.tools.db.schema.Column;
import com.soffid.tools.db.schema.Database;
import com.soffid.tools.db.schema.ForeignKey;
import com.soffid.tools.db.schema.Index;
import com.soffid.tools.db.schema.Sequence;
import com.soffid.tools.db.schema.Table;

public class PostgresqlUpdater extends DBUpdater{

	protected String translateType(Column c) {
		if (c.type.equals("LONGVARCHAR") || (c.type.equals("VARCHAR") && (Long.decode(c.length).longValue() > 4000)))
		{
			return "text";
		}
		else if (c.type.equals("DATE") || c.type.equals("DATETIME") || c.type.equals("TIMESTAMP"))
		{
			return "date";
		}
		else if (c.type.equals("LONG") || c.type.equals("BIGINT"))
		{
			return "bigint";
		}
		else if (c.type.equals("INTEGER") || c.type.equals("SMALLINT"))
		{
			return "integer";
		}
		else if (c.type.equals("FLOAT"))
		{
			return "real";
		}
		else if (c.type.equals("DOUBLE"))
		{
			return "double precision";
		}
		else if (c.type.equals("LONGVARBINARY") || c.type.equals("VARBINARY") || c.type.equals("BLOB"))
		{
			return "bytea";
		}
		else if (c.type.equals("BIT") || c.type.equals("BOOLEAN"))
		{
			return "boolean";
		}
		else if (c.type.equals("VARCHAR") && (new Integer(c.length) < 4001))
		{
			return "VARCHAR("+c.length+")";
		}
		else if (c.type.equals("VARCHAR") && (new Integer(c.length) >= 4001) || c.type.equals("CLOB"))
		{
			return "text";
		}
		else if (c.length != null && !c.length.isEmpty() && !(c.type.equals("BLOB") || c.type.equals("CLOB")))
		{	
			return c.type + "(" + c.length + ")";
		}
		else if (c.length != null && !c.length.isEmpty() && (c.type.equals("BLOB") || c.type.equals("CLOB")))
		{
			return c.type;
		}
			
		return super.translateType(c);
	}

	@Override
	protected boolean suportsAutoIncrement() {
		
		return false;
	}

	protected boolean isCaseSensitive () 
	{
		return false;
	}
	

	@Override
	protected void generateDropIndex(Index i, StringBuffer sb) {
		sb.append ("DROP ");
		sb.append ("INDEX ")
			.append (i.name);
	}

	@Override
	protected void generateDropConstraint(ForeignKey i, StringBuffer sb) {
		ForeignKey fk = (ForeignKey) i;
		sb.append("ALTER TABLE ")
			.append(fk.tableName)
			.append(" DROP CONSTRAINT ")
			.append(fk.name);
	}

	protected void updateColumn(Connection conn, Table t, Column c, Column old) throws SQLException {
		StringBuffer sb = new StringBuffer ();
		String t1 = translateType(c);
		if (c.notNull && ! old.notNull)
		{
			executeStatement (conn, String.format ("ALTER TABLE %s ALTER COLUMN %s SET NOT NULL",
				t.name, c.name));
		} else if (! c.notNull && old.notNull) {
			executeStatement (conn, String.format ("ALTER TABLE %s ALTER COLUMN %s DROP NOT NULL",
					t.name, c.name));
		}
		
		if(!(c.type.equals("LONGVARCHAR") && old.type.equals("CLOB"))) {
			String t2 = translateType(old);
			if (! t1.equals(t2))
			{
		
				executeStatement (conn, String.format ("ALTER TABLE %s ALTER COLUMN %s TYPE %s",
						t.name, c.name, t1));
			}
		}
	}

	protected void updateTable(Connection conn, Table t, Table old) throws SQLException {
		super.updateTable(conn, t, old);
	}

	
	protected void dropPrimaryKey(Connection conn, Table t, String pk) throws SQLException {
		super.dropPrimaryKey(conn,t,pk);
		for (Column c: t.columns)
		{
			ResultSet columns = conn.getMetaData().getColumns(null, getCurrentUser(conn), t.name.toUpperCase(), c.name.toUpperCase());
			if (columns.next())
				c.notNull = (columns.getShort(11) == DatabaseMetaData.attributeNoNulls);
			columns.close();
		}
	}
	@Override
	protected String getCurrentUser(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT current_user");
		try {
			if (rset.next())
				return rset.getString(1);
		} finally {
			rset.close ();
			stmt.close();
		}
		return null;
	}


	protected void createSequence(Connection conn, Sequence s) throws SQLException {
	}

	@Override
	protected void dropIndex(Connection conn, Index i) throws SQLException {
		String pkName = getPrimaryKeyName(i.tableName);
		if (!i.name.equalsIgnoreCase(pkName))
			super.dropIndex(conn, i);
	}

	@Override
	protected void generateAddColumnSentence(Table t, Column c, StringBuffer sb) {
		sb.append("ALTER TABLE ")
		.append(t.name)
		.append(" ADD COLUMN ");
		describeAlterColumn(c, sb);
	}
	
	protected void describeAlterColumn(Column c, StringBuffer sb) {
		sb.append (c.name)
			.append (' ')
			.append(translateType(c));
		if (c.autoIncrement && suportsAutoIncrement())
			sb.append (" ").append(getAutoIncrementClause());
	}
}
