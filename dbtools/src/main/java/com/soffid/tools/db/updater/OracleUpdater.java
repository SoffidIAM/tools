package com.soffid.tools.db.updater;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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

public class OracleUpdater extends DBUpdater{

	protected String translateType(Column c) {
		if (c.type.equals("LONGVARCHAR") || (c.type.equals("VARCHAR") && (Long.decode(c.length).longValue() > 4000)))
		{
			return "CLOB";
		}
		else if (c.type.equals("DATE") || c.type.equals("DATETIME") || c.type.equals("TIMESTAMP"))
		{
			return "date";
		}
		else if (c.type.equals("LONG") || c.type.equals("BIGINT"))
		{
			return "NUMBER(19,0)";
		}
		else if (c.type.equals("INTEGER") || c.type.equals("SMALLINT"))
		{
			return "NUMBER(9,0)";
		}
		else if (c.type.equals("FLOAT"))
		{
			return "NUMBER(9,9)";
		}
		else if (c.type.equals("DOUBLE"))
		{
			return "NUMBER(20,20)";
		}
		else if (c.type.equals("LONGVARBINARY"))
		{
			return "BLOB";
		}
		else if (c.type.equals("VARBINARY"))
		{
			return "RAW("+c.length+")";
		}
		else if (c.type.equals("BIT") || c.type.equals("BOOLEAN"))
		{
			return "NUMBER(1,0)";
		}
		else if (c.type.equals("VARCHAR") && (new Integer(c.length) < 4001))
		{
			return "VARCHAR2("+c.length+" CHAR)";
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
		if (c.notNull && ! old.notNull)
		{
			try{
				executeStatement (conn, String.format ("ALTER TABLE %s MODIFY %s NOT NULL",
					t.name, c.name));
			}catch(SQLException s){
				if(s.getErrorCode() == 1758){
					executeStatement (conn, String.format("ALTER TABLE %s MODIFY %s", 
					t.name, c.name));
				}
				else
					throw new SQLException(s);
			}
		}
		if (! c.notNull && old.notNull)
		{
			
			executeStatement (conn, String.format ("ALTER TABLE %s MODIFY %s NULL",
					t.name, c.name));
		}	
		if(!(c.type.equals("LONGVARCHAR") && old.type.equals("CLOB")))
		{
			String t1 = translateType(c);
			String t2 = translateType(old);
			if (! t1.equals(t2))
			{
		
				executeStatement (conn, String.format ("ALTER TABLE %s MODIFY %s %s",
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
		ResultSet rset = stmt.executeQuery("SELECT USER FROM DUAL");
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
		executeStatement(conn, "CREATE SEQUENCE "+s.name);
	}

	@Override
	protected void generateCreateIndexSentence(Index i, StringBuffer sb) {
		super.generateCreateIndexSentence(i, sb);
		if (System.getProperty("dbIndexTablespace") != null)
			sb.append (" TABLESPACE ")
			.append (System.getProperty("dbIndexTablespace"));
	}

	@Override
	protected void generateCreateTableSentence(Table t, StringBuffer sb) {
		super.generateCreateTableSentence(t, sb);
		if (System.getProperty("dbTableTablespace") != null)
			sb.append (" TABLESPACE ")
				.append (System.getProperty("dbTableTablespace"));
	}


	protected void generatePrimaryKeyConstraint(Table t, StringBuffer sb,
			String pk) {
		super.generatePrimaryKeyConstraint(t, sb, pk);
		if (System.getProperty("dbIndexTablespace") != null)
			sb.append (" USING INDEX TABLESPACE " )
				.append (System.getProperty("dbIndexTablespace"));
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
	
}
