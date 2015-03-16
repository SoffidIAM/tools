package com.soffid.tools.db.updater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.soffid.tools.db.schema.Column;
import com.soffid.tools.db.schema.ForeignKey;
import com.soffid.tools.db.schema.Index;
import com.soffid.tools.db.schema.Table;

/**
 * @author (C) Soffid 2013
 *
 */
public class MsSqlServerUpdater extends DBUpdater
{
	protected String translateType(Column c) {
		if (c.type.equals ("BLOB"))
		{
			return "varbinary(max)";
		}
		else if (c.type.equals("CLOB") || c.type.equals("LONGVARCHAR") ||
				(c.type.equals("VARCHAR") && Integer.parseInt(c.length) > 8000))
		{
			return "nvarchar(max)";
		}
		else if (c.type.equals("DATE") || c.type.equals("DATETIME") || c.type.equals("TIMESTAMP"))
		{
			return "datetime";
		}
		else if (c.type.equals("LONG"))
		{
			return "bigint";
		}
		else if (c.type.equals("FLOAT") || c.type.equals("DOUBLE"))
		{
			return "real";
		}
		else if (c.type.equals("BOOLEAN"))
		{
			return "bit";
		}
		else if (c.type.equals("INTEGER"))
		{
			return "int";
		}
		
		return super.translateType(c);
	}

	@Override
	protected String getCurrentUser(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT SYSTEM_USER");
		try {
			if (rset.next())
			{
				String u = rset.getString(1);
				int i = u.indexOf('@');
				if ( i > 0)
					u = u.substring(0, i);
//				return u;
			}
		} finally {
			rset.close ();
			stmt.close();
		}
		return null;
	}

	protected String getAutoIncrementClause() 
	{
		return "identity(1,1)";
	}
	
	@Override
	protected void updateColumn(Connection conn, Table t, Column c,
			Column oldColumn) throws SQLException
	{
		StringBuffer sb = new StringBuffer ();
		List<Index> indexes = new LinkedList<Index>();
		
		if (!c.primaryKey)
		{
			// Search table indexes
			for (Index index : actual.indexes)
			{
				if (index.tableName.equals(t.name) && 
					index.columns.contains(c.name))
				{
					StringBuffer sb2 = new StringBuffer();
					generateDropIndexSentence(t, index, sb2);
					indexes.add(index);
					executeStatement(conn, sb2.toString());
				}
			}
		}
		
		try
		{
			generateAlterColumnSentence (t, c, sb, false);
			executeStatement (conn, sb.toString());
		}
		
		catch (SQLException e)
		{
			if (e.getErrorCode() == 515)
			{
				sb = new StringBuffer();
				generateAlterColumnSentence(t, c, sb, true);
				executeStatement (conn, sb.toString());
			}
			
			else
				throw new SQLException(e);
		}

		for (Index index : indexes)
		{
			StringBuffer sb2 = new StringBuffer();
			generateCreateIndexSentence(t, index, c, sb2);
			executeStatement(conn, sb2.toString());
		}
	}
	
	protected void generateAlterColumnSentence(Table t, Column c,
		StringBuffer sb, boolean ignoreNulls)
	{
		sb.append("ALTER TABLE ")
		.append(t.name)
		.append(" ALTER COLUMN ");
		sb.append (c.name)
		.append (' ')
		.append(translateType(c));
		
		if (!ignoreNulls)
		{
			if (c.notNull)
				sb.append (" not null; ");
			else
				sb.append(" null; ");
		}
	}
	
	@Override
	protected void generateAddColumnSentence(Table t, Column c, StringBuffer sb) {
		sb.append("ALTER TABLE ")
		.append(t.name)
		.append(" ADD ");
		describeAlterColumn(c, sb);
	}

	protected void describeAlterColumn(Column c, StringBuffer sb) {
		sb.append (c.name)
			.append (' ')
			.append(translateType(c));
		if (c.autoIncrement && suportsAutoIncrement())
			sb.append (" ").append(getAutoIncrementClause());
	}

	/**
	 * @param t
	 * @param sb
	 */
	private void generateCreateIndexSentence(Table t, Index index, Column c, StringBuffer sb)
	{
		sb.append("CREATE");
		
		if (index.unique)
			sb.append(" UNIQUE ");
		
		sb.append(" INDEX ")
		.append(index.name)
		.append(" ON ")
		.append(index.tableName)
		.append("(")
		.append(c.name)
		.append(")");
	}

	/**
	 * @param t
	 * @param index 
	 * @param sb
	 */
	private void generateDropIndexSentence(Table t, Index index, StringBuffer sb)
	{
		sb.append("DROP INDEX ")
		.append(index.name)
		.append(" ON ")
		.append(t.name)
		.append(" ");
	}

	@Override
	protected void generateDropConstraint(ForeignKey fk, StringBuffer sb) {
		sb.append ("ALTER TABLE ")
			.append (fk.tableName)
			.append(" DROP ")
			.append (fk.name);
	}

	@Override
	protected void dropIndex(Connection conn, Index i) throws SQLException {
		String pkName = getPrimaryKeyName(i.tableName);
		if (!i.name.equalsIgnoreCase(pkName))
			super.dropIndex(conn, i);
	}

	@Override
	protected void generateCreateIndexSentence(Index i, StringBuffer sb) {
		super.generateCreateIndexSentence(i, sb);
		if (i.unique)
		{
			sb.append(" WHERE ");
			boolean first = true;
			for (String col: i.columns)
			{
				if (first) first = false;
				else sb.append (" AND ");
				sb.append (col).append(" IS NOT NULL");
			}
		}
	}
}
