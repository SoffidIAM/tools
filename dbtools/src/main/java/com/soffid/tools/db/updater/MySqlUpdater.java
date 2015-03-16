package com.soffid.tools.db.updater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.soffid.tools.db.schema.Column;
import com.soffid.tools.db.schema.ForeignKey;
import com.soffid.tools.db.schema.Index;
import com.soffid.tools.db.schema.Table;

public class MySqlUpdater extends DBUpdater{

	protected void dropPrimaryKey(Connection conn, Table t, String pk) throws SQLException {
		executeStatement (conn, String.format ("ALTER TABLE %s DROP PRIMARY KEY",
				t.name, t.name, pk));
	}

	protected void generateDropConstraint(Index i, StringBuffer sb) {
		ForeignKey fk = (ForeignKey) i;
		sb.append("ALTER TABLE ")
			.append(fk.tableName)
			.append(" DROP KEY ")
			.append(fk.name);
	}

	protected String translateType(Column c) {
		if (c.type.equals ("BLOB"))
		{
			if (c.length == null || c.length.isEmpty())
				return "mediumblob";
			else if (Long.decode(c.length).longValue() < 60000)
				return "blob";
			else if (Long.decode(c.length).longValue() < 120000)
				return "mediumblob";
			else
				return "longblob";
		}
		else if (c.type.equals("CLOB") || c.type.equals("LONGVARCHAR"))
		{
			if (c.length == null || c.length.isEmpty())
				return "mediumtext";
			else if (Long.decode(c.length).longValue() < 60000)
				return "text";
			else if (Long.decode(c.length).longValue() < 120000)
				return "mediumtext";
			else
				return "longtext";
		}
		else if (c.type.equals("DATE") || c.type.equals("DATETIME"))
		{
			return "datetime";
		}
		else if (c.type.equals("LONG"))
		{
			return "bigint";
		}
		else if (c.type.equals("INTEGER"))
		{
			return "int";
		}
		else if (c.type.equals("BOOLEAN"))
		{
			return "bit";
		}
		else if(c.type.equals("VARCHAR"))
		{
			if(c.length != null && Long.decode(c.length).longValue() > 21844)
				return "text";
		}
			
		return super.translateType(c);
	}

	@Override
	protected String getCurrentUser(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT USER()");
		try {
			if (rset.next())
			{
				String u = rset.getString(1);
				int i = u.indexOf('@');
				if ( i > 0)
					u = u.substring(0, i);
				return u;
			}
		} finally {
			rset.close ();
			stmt.close();
		}
		return null;
	}

	@Override
	protected void dropIndex(Connection conn, Index i) throws SQLException {
		if (!i.name.equals("PRIMARY"))
			super.dropIndex(conn, i);
	}

	protected void generateCreateTableSentence(Table t, StringBuffer sb) {
		super.generateCreateTableSentence(t, sb);
		sb.append (" Engine=InnoDB");
	}

	protected void generateAlterColumnSentence(Table t, Column c, StringBuffer sb) {
		super.generateAlterColumnSentence(t, c, sb);
		if (c.autoIncrement && c.primaryKey)
			sb.append (" PRIMARY KEY");
		
	}
	
	@Override	
	protected void generateAddColumnSentence(Table t, Column c, StringBuffer sb) {
		super.generateAddColumnSentence(t, c, sb);
		if (c.autoIncrement && c.primaryKey)
			sb.append (" PRIMARY KEY");
		
	}
	

	protected void createPrimaryKey(Connection conn, Table t, String pk) throws SQLException {
		try {
			super.createPrimaryKey(conn, t, pk);
		} catch (SQLException e) {
			if (e.getErrorCode() != 1068) // Primary key already exists
				throw e;
		}
	}

}
