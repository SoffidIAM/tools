package com.soffid.tools.db.updater;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;

import javax.print.attribute.SupportedValuesAttribute;

import com.soffid.tools.db.persistence.DbReader;
import com.soffid.tools.db.schema.Column;
import com.soffid.tools.db.schema.Database;
import com.soffid.tools.db.schema.ForeignKey;
import com.soffid.tools.db.schema.Index;
import com.soffid.tools.db.schema.Sequence;
import com.soffid.tools.db.schema.Table;

public abstract class DBUpdater {
	boolean ignoreFailures = false;
	Database actual;
	Database target;

	public boolean isIgnoreFailures() {
		return ignoreFailures;
	}

	public void setIgnoreFailures(boolean ignoreFailures) {
		this.ignoreFailures = ignoreFailures;
	}

	PrintStream log;

	public PrintStream getLog() {
		return log;
	}

	public void setLog(PrintStream log) {
		this.log = log;
	}

	public void update(Connection conn, Database db) throws Exception {
		update(conn, db, false);
	}

	protected void update(Connection conn, Database db,
			boolean ignoreForeignKeys) throws Exception {
		log.println("Retrieving existing database objects");
		DbReader dbr = new DbReader();
		dbr.setLog(log);
		actual = dbr.parse(conn, getCurrentUser(conn));
		log.println("Got existing database objects");
		initialFixups(actual);
		initialFixups(db);
		// First create / update all tables
		log.println("Removing no longer needed indexes");
		removeForeignKeys(conn, actual, db);
		removeIndexes(conn, actual, db);
		log.println("Updating tables");
		updateTables(conn, actual, db);
		log.println("Updating indexes");
		updateIndexes(conn, actual, db);
		if (!ignoreForeignKeys)
			updateForeignKeys(conn, actual, db);
		log.println("Updating sequences");
		updateSequences(conn, actual, db);
		log.println("Data model verified");
	}

	String getIndexHash (Index index)
	{
		StringBuffer sb = new StringBuffer ();
		sb.append (index.tableName).append(" ");
		// Search for a matching (non unique) index
		for (int i = 0; i < index.columns.size(); i++) {
			sb.append(index.columns.get(i)).append(" ");
		}
		return sb.toString();
	}
	/**
	 * @param db
	 */
	private void initialFixups(Database db) {
		HashSet<String> uniqueIndexes = new HashSet<String>();
		// Look for any index
		for (Iterator<Index> it = db.indexes.iterator(); it.hasNext();) {
			Index current = it.next();
			// that is unique
			if (current.unique) {
				uniqueIndexes.add(getIndexHash(current));
			}
		}
		// Now remove collisioning indexes
		for (Iterator<Index> it = db.indexes.iterator(); it.hasNext();) {
			Index current = it.next();
			// that is unique
			if (! current.unique) {
				String hash = getIndexHash(current);
				if (uniqueIndexes.contains(hash))
					it.remove();
			}
		}
	}

	public void updateIgnoreForeignKeys(Connection conn, Database db)
			throws Exception {
		update(conn, db, true);
	}

	protected void removeIndexes(Connection conn, Database actual, Database db)
			throws SQLException {
		for (Index i : actual.indexes) {
			if (!i.name.toUpperCase().startsWith("Z")) {
				Index newIndex = db.findIndex(i.tableName, i.name,
						isCaseSensitive());
				if (newIndex == null) {
					Table t = db.findTable(i.tableName, isCaseSensitive());
					if (t != null)
						dropIndex(conn, i);
				}
			}
		}
	}

	protected void removeForeignKeys(Connection conn, Database actual,
			Database db) throws SQLException {
		for (ForeignKey i : actual.foreignKeys) {
			if (!i.name.toUpperCase().startsWith("Z")) {
				ForeignKey newIndex = db.findForeignKey(i.tableName, i.name,
						isCaseSensitive());
				if (newIndex == null) {
					Table t = db.findTable(i.tableName, isCaseSensitive());
					if (t != null)
						dropForeignKey(conn, i);
				}
			}
		}
	}

	public void updateSequences(Connection conn, Database actual, Database db)
			throws SQLException {
		for (Sequence s : db.sequences) {
			boolean found = false;
			for (Sequence s2 : actual.sequences) {
				if (equalsCase(s.name, s2.name)) {
					found = true;
					break;
				}
			}
			if (!found)
				createSequence(conn, s);
		}
	}

	protected void createSequence(Connection conn, Sequence s)
			throws SQLException {
	}

	boolean equalsCase(String s1, String s2) {
		if (s1 == null && s2 == null)
			return true;
		else if (s1 == null && s2 != null)
			return false;
		else if (isCaseSensitive())
			return s1.equals(s2);
		else
			return s1.equalsIgnoreCase(s2);
	}

	protected void updateTables(Connection conn, Database actual, Database db)
			throws SQLException {
		for (Table t : db.tables) {
			Table old = actual.findTable(t.name, isCaseSensitive());
			if (old == null) {
				createTable(conn, t);
			} else {
				updateTable(conn, t, old);
			}
		}
	}

	protected void updateIndexes(Connection conn, Database actual, Database db)
			throws SQLException {
		for (Index i : db.indexes) {
			Index old = actual
					.findIndex(i.tableName, i.name, isCaseSensitive());
			if (old == null) {
				createIndex(conn, i);
			} else {
				updateIndex(conn, i, old);
			}
		}
	}

	protected void updateForeignKeys(Connection conn, Database actual,
			Database db) throws SQLException {
		for (ForeignKey i : db.foreignKeys) {
			ForeignKey old = actual.findForeignKey(i.tableName, i.name,
					isCaseSensitive());
			if (old == null) {
				createForeignKey(conn, i);
			} else {
				updateForeignKey(conn, i, old);
			}
		}
	}

	protected void updateIndex(Connection conn, Index i, Index old)
			throws SQLException {
		boolean changes = false;
		if (i.columns.size() != old.columns.size())
			changes = true;
		if (!changes) {
			Iterator<String> it1 = i.columns.iterator();
			Iterator<String> it2 = old.columns.iterator();
			while (it1.hasNext()) {
				if (!equalsCase(it1.next(), it2.next())) {
					changes = true;
					break;
				}
			}
		}
		if (! changes)
		{
			changes = i.unique != old.unique;
		}

		if (changes) {
			dropIndex(conn, old);
			createIndex(conn, i);
		}
	}

	protected void updateForeignKey(Connection conn, ForeignKey fk,
			ForeignKey old) throws SQLException {
		boolean changes = false;
		if (fk.columns.size() != old.columns.size())
			changes = true;
		if (!changes) {
			Iterator<String> it1 = fk.columns.iterator();
			Iterator<String> it2 = old.columns.iterator();
			while (it1.hasNext()) {
				if (!equalsCase(it1.next(), it2.next())) {
					changes = true;
					break;
				}
			}
		}
		if (!changes) {
			if (!equalsCase(fk.tableName, old.tableName)) {
				changes = true;
			} else {
				Iterator<String> it1 = fk.columns.iterator();
				Iterator<String> it2 = old.columns.iterator();
				while (it1.hasNext()) {
					if (!equalsCase(it1.next(), it2.next())) {
						changes = true;
						break;
					}
				}
			}
		}
		if (changes) {
			dropForeignKey(conn, old);
			createForeignKey(conn, fk);
		}
	}

	protected void dropIndex(Connection conn, Index i) throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateDropIndex(i, sb);
		try {
			executeStatement(conn, sb.toString());
		} catch (SQLException e) {
			if (log == null)
				System.out.println("ERROR droping index " + e.getErrorCode()
						+ ":" + e.getMessage());
			else
				log.println("ERROR droping index " + e.getErrorCode() + ":"
						+ e.getMessage());
		}
	}

	protected void dropForeignKey(Connection conn, ForeignKey fk)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateDropConstraint(fk, sb);
		executeStatement(conn, sb.toString());
	}

	protected void createIndex(Connection conn, Index i) throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateCreateIndexSentence(i, sb);
		executeStatement(conn, sb.toString());
	}

	protected void createForeignKey(Connection conn, ForeignKey fk)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateCreateConstraintSentence(fk, sb);
		executeStatement(conn, sb.toString());
	}

	protected void generateCreateIndexSentence(Index i, StringBuffer sb) {
		sb.append("CREATE ");
		if (i.unique)
			sb.append("UNIQUE ");
		sb.append("INDEX ").append(i.name).append(" ON ").append(i.tableName)
				.append("(");
		boolean first = true;
		for (String col : i.columns) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(col);
		}
		sb.append(")");
	}

	protected void generateCreateConstraintSentence(Index i, StringBuffer sb) {
		ForeignKey fk = (ForeignKey) i;
		sb.append("ALTER TABLE ").append(fk.tableName)
				.append(" ADD CONSTRAINT ").append(fk.name)
				.append(" FOREIGN KEY (");
		boolean first = true;
		for (String col : i.columns) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(col);
		}
		sb.append(") REFERENCES ").append(fk.foreignTable).append("(");
		first = true;
		for (String col : fk.foreignKeyColumns) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(col);
		}
		sb.append(")");
	}

	protected void generateDropIndex(Index i, StringBuffer sb) {
		sb.append("DROP ");
		sb.append("INDEX ").append(i.name).append(" ON ").append(i.tableName);
	}

	protected void generateDropConstraint(ForeignKey fk, StringBuffer sb) {
		sb.append("ALTER TABLE ").append(fk.tableName)
				.append(" DROP FOREIGN KEY ").append(fk.name);
	}

	protected boolean suportsAutoIncrement() {
		return true;
	}

	protected void dropPrimaryKey(Connection conn, Table t, String pk)
			throws SQLException {
		executeStatement(conn, String.format(
				"ALTER TABLE %s DROP CONSTRAINT %s", t.name,
				getPrimaryKeyName(t)));
	}

	protected void createPrimaryKey(Connection conn, Table t, String pk)
			throws SQLException {
		executeStatement(conn, generatePrimaryKeySentence(t, pk));
	}

	protected String generatePrimaryKeySentence(Table t, String pk) {
		StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ").append(t.name).append(" ADD ");
		generatePrimaryKeyConstraint(t, sb, pk);
		return sb.toString();
	}

	protected void updateTable(Connection conn, Table t, Table old)
			throws SQLException {
		// First, delete old primary keys
		String newPK = t.getPrimaryKey();
		String oldPK = old.getPrimaryKey();

		// First disable not needed auto_increment columns
		for (Column oldColumn : old.columns) {
			if (oldColumn.autoIncrement) {
				boolean found = false;
				for (Column newColumn : t.columns) {
					if (equalsCase(oldColumn.name, newColumn.name)) {
						found = true;
						break;
					}
				}
				if (!found) {
					Column c = new Column();
					c.autoIncrement = false;
					c.length = oldColumn.length;
					c.name = oldColumn.name;
					c.notNull = oldColumn.notNull;
					c.primaryKey = oldColumn.primaryKey;
					c.type = oldColumn.type;
					updateColumn(conn, t, c, oldColumn);
					oldColumn.autoIncrement = false;
				}
			}
		}

		// Drop primary key if needed
		if (!equalsCase(oldPK, newPK) && oldPK != null)
			dropPrimaryKey(conn, old, oldPK);

		// Next disable not needed columns
		for (Column oldColumn : old.columns) {
			if (oldColumn.notNull) {
				boolean found = false;
				for (Column newColumn : t.columns) {
					if (equalsCase(oldColumn.name, newColumn.name)) {
						found = true;
						break;
					}
				}
				if (!found) {
					Column c = new Column();
					c.autoIncrement = oldColumn.autoIncrement;
					c.length = oldColumn.length;
					c.name = oldColumn.name;
					c.notNull = false;
					c.primaryKey = oldColumn.primaryKey;
					c.type = oldColumn.type;
					updateColumn(conn, t, c, oldColumn);
					oldColumn.notNull = false;
				}
			}
		}
		// Create missing columns
		for (Column newColumn : t.columns) {
			boolean found = false;
			for (Column oldColumn : old.columns) {
				if (equalsCase(oldColumn.name, newColumn.name)) {
					boolean anyChange = false;
					found = true;
					if (oldColumn.length != null && newColumn.length != null
							&& !oldColumn.length.isEmpty()
							&& !newColumn.length.isEmpty()) {
						double oldSize = Double.parseDouble(oldColumn.length);
						double newSize = Double.parseDouble(newColumn.length);
						if (newSize > oldSize)
							anyChange = true;
					}
					if (suportsAutoIncrement()
							&& oldColumn.autoIncrement != newColumn.autoIncrement)
						anyChange = true;
					if (oldColumn.notNull != newColumn.notNull)
						anyChange = true;
					if (anyChange) {
						updateColumn(conn, t, newColumn, oldColumn);
					}
					break;
				}
			}
			if (!found)
				createColumn(conn, t, newColumn);
		}
		// Next create new primary keys
		if (!equalsCase(oldPK, newPK) && newPK != null)
			createPrimaryKey(conn, t, newPK);
	}

	private void createColumn(Connection conn, Table t, Column c)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateAddColumnSentence(t, c, sb);
		executeStatement(conn, sb.toString());
	}

	protected void updateColumn(Connection conn, Table t, Column c,
			Column oldColumn) throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateAlterColumnSentence(t, c, sb);
		executeStatement(conn, sb.toString());
	}

	protected void generateAddColumnSentence(Table t, Column c, StringBuffer sb) {
		sb.append("ALTER TABLE ").append(t.name).append(" ADD ");
		describeColumn(c, sb);
	}

	protected void generateAlterColumnSentence(Table t, Column c,
			StringBuffer sb) {
		sb.append("ALTER TABLE ").append(t.name).append(" MODIFY ");
		describeColumn(c, sb);
	}

	protected void createTable(Connection conn, Table t) throws SQLException {
		StringBuffer sb = new StringBuffer();
		generateCreateTableSentence(t, sb);
		executeStatement(conn, sb.toString());
	}

	protected void generateCreateTableSentence(Table t, StringBuffer sb) {
		sb.append("CREATE TABLE ").append(t.name).append(" (");
		boolean first = true;
		String pk = null;
		for (Column c : t.columns) {
			if (first)
				first = false;
			else
				sb.append(", ");
			describeColumn(c, sb);
			if (c.primaryKey) {
				if (pk == null)
					pk = c.name;
				else
					pk = pk + ", " + c.name;
			}
		}
		if (pk != null) {
			sb.append(',');
			generatePrimaryKeyConstraint(t, sb, pk);
		}

		sb.append(')');
	}

	protected void generatePrimaryKeyConstraint(Table t, StringBuffer sb,
			String pk) {
		sb.append(" CONSTRAINT ").append(getPrimaryKeyName(t))
				.append(" PRIMARY KEY (").append(pk).append(")");
	}

	protected String getPrimaryKeyName(Table t) {
		return getPrimaryKeyName(t.name);
	}

	protected String getPrimaryKeyName(String tableName) {
		if (tableName.length() > 25)
			return tableName.substring(0, 25) + "_PK";
		else
			return tableName + "_PK";
	}

	protected void executeStatement(Connection conn, String string)
			throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			if (log == null)
				System.out.println("*** " + string);
			else
				log.println(string);
			stmt.execute(string);
		} catch (SQLException e) {
			if (ignoreFailures) {
				if (log == null)
					System.out.println("ERROR " + e.getErrorCode() + ": "
							+ e.getMessage());
				else
					log.println("ERROR " + e.getErrorCode() + ": "
							+ e.getMessage());
			} else
				throw e;
		} finally {
			stmt.close();
		}
	}

	protected String getAutoIncrementClause() {
		return "auto_increment";
	}

	protected void describeColumn(Column c, StringBuffer sb) {
		sb.append(c.name).append(' ').append(translateType(c));
		if (c.notNull)
			sb.append(" not null");
		else
			sb.append(" null");
		if (c.autoIncrement && suportsAutoIncrement())
			sb.append(" ").append(getAutoIncrementClause());
	}

	protected String translateType(Column c) {
		if (c.length != null && !c.length.isEmpty())
			return c.type + "(" + c.length + ")";
		else
			return c.type;
	}

	protected boolean isCaseSensitive() {
		return true;
	}

	protected abstract String getCurrentUser(Connection conn)
			throws SQLException;
}
