package com.soffid.tools.db.persistence;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.soffid.tools.db.schema.*;


public class DbReader {
	PrintStream log  = null;
	
	public PrintStream getLog() {
		return log;
	}

	public void setLog(PrintStream log) {
		this.log = log;
	}

	public Database parse (Connection conn, String schema) throws Exception
	{
		Database db = new Database();
		DatabaseMetaData metadata = conn.getMetaData();
		try {
			if (log != null)
				log.println ("Database model :"+metadata.getDatabaseProductName());
			if (metadata.getDatabaseProductName().equalsIgnoreCase("PostgreSQL"))
				schema = "public";
			if (log != null)
				log.println("Getting sequences ....");
			ResultSet rset = conn.prepareStatement("SELECT SEQUENCE_NAME FROM USER_SEQUENCES").executeQuery();
			while (rset.next())
			{
				String name = rset.getString(1);
				Sequence seq = new Sequence();
				seq.name = name;
				db.sequences.add(seq);
//				if (log != null)
//					log.println ("  > "+name);
			}
		} catch (SQLException e) {
			// No sequences available
		}
		if (log != null)
			log.println ("Getting tables for "+schema+"....");
		ResultSet tables = getTables(schema, metadata);
		while (tables.next())
		{
			String catalog = tables.getString(1);
			String tableSchema = tables.getString(2);
			String tableName =tables.getString(3);
			String tableType = tables.getString(4);
//			if (log != null)
//				log.println ("  > "+ tableSchema+"."+tableName);
			if (tableType.contains ("TABLE"))
			{
//				if (log != null)
//					log.println ("  > "+tableName+ " columns");
				Table t = new Table();
				t.name = tableName;
				try {
					ResultSet columns = getTableColumns(schema, metadata, catalog, tableName);
					while (columns.next())
					{
						Column c = new Column();
						c.name = columns.getString(4);
						c.length =  columns.getString(7);
						String decimalDigits = columns.getString(9);
						if (decimalDigits != null && !decimalDigits.equals("0"))
							c.length = c.length + "." + decimalDigits;
						int type = columns.getShort(5);
						switch (type)
						{
						case Types.BIGINT:
							c.length = null;
							c.type = "BIGINT";
							break;
						case Types.BINARY:
							c.type = "BINARY";
							break;
						case Types.BIT:
							c.length = null;
							c.type = "BIT";
							break;
						case Types.BLOB:
							c.type = "BLOB";
						case Types.BOOLEAN:
							c.length = null;
							c.type = "BOOLEAN";
							break;
						case Types.CHAR:
							c.type = "CHAR";
							break;
						case Types.CLOB:
							c.type = "CLOB";
							break;
						case Types.DATE:
							c.length = null;
							c.type = "DATE";
							break;
						case Types.DECIMAL:
							c.type = "DECIMAL";
							break;
						case Types.DOUBLE:
							c.length = null;
							c.type = "DOUBLE";
							break;
						case Types.FLOAT:
							c.length = null;
							c.type = "FLOAT";
							break;
						case Types.INTEGER:
							c.length = null;
							c.type = "INTEGER";
							break;
						case Types.JAVA_OBJECT:
							c.type = "JAVA_OBJECT";
							break;
						case Types.LONGNVARCHAR:
							c.type = "LONGVARCHAR";
							break;
						case Types.LONGVARBINARY:
							c.type = "LONGVARBINARY";
							break;
						case Types.LONGVARCHAR:
							c.type = "LONGVARCHAR";
							break;
						case Types.NCHAR:
							c.type = "NCHAR";
							break;
						case Types.NCLOB:
							c.type = "NCLOB";
							break;
						case Types.NULL:
							c.type = "NULL";
							break;
						case Types.NUMERIC:
							c.type = "NUMERIC";
							break;
						case Types.NVARCHAR:
							c.type = "NVARCHAR";
							break;
						case Types.REAL:
							c.type = "REAL";
							break;
						case Types.SMALLINT:
							c.length = null;
							c.type = "SMALLINT";
							break;
						case Types.SQLXML:
							c.type = "SQLXML";
							break;
						case Types.STRUCT:
							c.type = "STRUCT";
							break;
						case Types.TIME:
							c.length = null;
							c.type = "TIME";
							break;
						case Types.TIMESTAMP:
							c.length = null;
							c.type = "TIMESTAMP";
							break;
						case Types.TINYINT:
							c.length = null;
							c.type = "TINYINT";
							break;
						case Types.VARBINARY:
							c.type = "VARBINARY";
							break;
						case Types.VARCHAR:
							c.type = "VARCHAR";
							break;
						default:
							c.type = "Unknown";
							break;
						}
						c.notNull = (columns.getShort(11) == DatabaseMetaData.attributeNoNulls);
						try {
							c.autoIncrement = ("YES".equals(columns.getString(23)));
						} catch (SQLException e) {
							c.autoIncrement = false;
						}
						t.columns.add(c);
						
					}
					columns.close();
					//
					// PRIMARY KEY
					//
//					if (log != null)
//						log.println ("  > "+tableName+ " primary keys");
					ResultSet pk = getPrimaryKeys(schema, metadata, catalog, tableName);
					String pkName = null;
					while (pk.next())
					{
						String column =  pk.getString(4);
						pkName = pk.getString(6);
						for (Column c: t.columns)
						{
							if (c.name.equals (column))
								c.primaryKey = true;
						}
					}
					pk.close();
					//
					// FOREIGN KEYS
					//
					
//					if (log != null)
//						log.println ("  > "+tableName+ " foreign keys");
					ResultSet fk = getImportedKeys(schema, metadata, catalog, tableName);
					Map<String, ForeignKey> foreignKeysMap = new HashMap<String, ForeignKey>();
					while (fk.next())
					{
						String fkName = fk.getString(12);
						ForeignKey foreign = foreignKeysMap.get(fkName);
						if (foreign == null)
						{
							foreign = new ForeignKey();
							foreign.name = fkName;
							foreign.tableName = tableName;
							foreign.foreignTable = fk.getString(3);
							foreignKeysMap.put(fkName, foreign);
							db.foreignKeys.add(foreign);
						}
						int order = fk.getShort(9) - 1;
						String column =  fk.getString(8);
						String refColumn = fk.getString(4);
						if (foreign.columns.size() <= order)
							foreign.columns.setSize(order+1);
						foreign.columns.set(order, column);
						if (foreign.foreignKeyColumns.size() <= order)
							foreign.foreignKeyColumns.setSize(order+1);
						foreign.foreignKeyColumns.set(order, refColumn);
					}
					fk.close ();
					//
					
//					if (log != null)
//						log.println ("  > "+tableName+ " indexes");
					ResultSet indexos = getIndexes(schema, metadata, catalog, tableName);
					Index currentIndex = null;
					while (indexos.next())
					{
						String indexName = indexos.getString(6);
						String columnName = indexos.getString(9);
//						if (log != null)
//							log.println ("  > "+tableName+ " indexes "+indexName+ " "+columnName);
						int order = indexos.getShort(8)-1;
						if (currentIndex != null && 
								! indexName.equals(currentIndex.name))
							currentIndex = null;
						if (order == 0)
						{
							currentIndex = new Index();
							db.indexes.add(currentIndex);
							currentIndex.name = indexName;
							currentIndex.tableName = tableName;
							Object obj = indexos.getObject(4);
//							if (log != null)
//								log.println ("  > "+tableName+ " indexes "+indexName+ " "+columnName+" unique "+obj);
							currentIndex.unique = obj.equals(Boolean.FALSE) || obj.toString().equals("0"); 
						}
						if (currentIndex != null)
						{
							if (currentIndex.columns.size() <= order)
								currentIndex.columns.setSize(order+1);
							currentIndex.columns.set(order, columnName);
						}
					}
					indexos.close();
//					if (log != null)
//						log.println ("  > "+tableName+ " done");
					db.tables.add(t);
				} catch (SQLException e) {
					if (e.getMessage().startsWith ("ORA-01031")) // Ignore insufficient permissions
					{
						
					}
					else
						System.out.println ("Error parsing "+t.name+": "+e.getErrorCode()+"-"+e.getMessage());
				}
			}
			
		}
		if (indexStmt != null)
		{
			indexStmt.close();
			indexStmt = null;
		}
		return db;
	}

	PreparedStatement indexStmt = null;
	private PreparedStatement importedKeysStmt;
	protected ResultSet getIndexes(String schema, DatabaseMetaData metadata,
			String catalog, String tableName) throws SQLException {
		try {
			if (indexStmt == null)
				indexStmt = metadata.getConnection().prepareStatement(
					"select user, user, I.TABLE_NAME, DECODE(UNIQUENESS,'UNIQUE', 0, 1), I.INDEX_TYPE, I.INDEX_NAME, DECODE(UNIQUENESS,'UNIQUE', 0, 1), C.COLUMN_POSITION, COLUMN_NAME "
					+ "FROM USER_INDEXES I, USER_IND_COLUMNS C "
					+ "WHERE  I.INDEX_NAME=C.INDEX_NAME AND I.TABLE_NAME=C.TABLE_NAME AND I.TABLE_NAME=? "
					+ "ORDER BY I.INDEX_NAME, C.COLUMN_POSITION");
			else
				indexStmt.clearParameters();
			try {
				indexStmt.setString(1, tableName);
				return indexStmt.executeQuery();
			} finally {
//				if (log != null)
//					log.println ("  > "+tableName+ " end");
			}
		} catch (SQLException e) {
			return metadata.getIndexInfo(catalog, schema, tableName, false, false);
		}
	}

	protected ResultSet getImportedKeys(String schema, DatabaseMetaData metadata,
			String catalog, String tableName) throws SQLException {
		return metadata.getImportedKeys(catalog, schema, tableName);
	}

	protected ResultSet getPrimaryKeys(String schema, DatabaseMetaData metadata,
			String catalog, String tableName) throws SQLException {
		return metadata.getPrimaryKeys(catalog, schema, tableName);
	}

	protected ResultSet getTableColumns(String schema, DatabaseMetaData metadata,
			String catalog, String tableName) throws SQLException {
		return metadata.getColumns(catalog, schema, tableName, null);
	}

	protected ResultSet getTables(String schema, DatabaseMetaData metadata)
			throws SQLException {
		return metadata.getTables(null, schema, null, new String[]{"TABLE"});
	}

}
