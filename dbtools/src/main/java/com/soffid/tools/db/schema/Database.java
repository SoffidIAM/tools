package com.soffid.tools.db.schema;

import java.io.Serializable;
import java.util.LinkedList;

public class Database implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Database() {
		super();
		tables = new LinkedList<Table>();
		indexes = new LinkedList<Index>();
		foreignKeys = new LinkedList<ForeignKey>();
		sequences = new LinkedList<Sequence>();
	}
	public LinkedList<ForeignKey> foreignKeys;
	public LinkedList<Table> tables;
	public LinkedList<Index> indexes;
	public LinkedList<Sequence> sequences;
	public Table findTable(String name, boolean caseSensitive) {
		for (Table t: tables)
		{
			if (caseSensitive && t.name.equals (name) ||
				!caseSensitive && t.name.equalsIgnoreCase(name))
				return t;
		}
		return null;
	}
	public Index findIndex(String tableName, String name, boolean caseSensitive) {
		for (Index i: indexes)
		{
			if (caseSensitive && i.tableName.equals (tableName) ||
					!caseSensitive && i.tableName.equalsIgnoreCase(tableName))
			{
				if (caseSensitive && i.name.equals (name) ||
						!caseSensitive && i.name.equalsIgnoreCase(name))
					return i;
				
			}
		}
		return null;
	}
	public ForeignKey findForeignKey(String tableName, String name, boolean caseSensitive) {
		for (ForeignKey i: foreignKeys)
		{
			if (caseSensitive && i.tableName.equals (tableName) ||
					!caseSensitive && i.tableName.equalsIgnoreCase(tableName))
			{
				if (caseSensitive && i.name.equals (name) ||
						!caseSensitive && i.name.equalsIgnoreCase(name))
					return i;
				
			}
		}
		return null;
	}
	public Sequence findSequence(String name, boolean caseSensitive) {
		for (Sequence s: sequences)
		{
			if (caseSensitive && s.name.equals (name) ||
					!caseSensitive && s.name.equalsIgnoreCase(name))
			{
				return s;
				
			}
		}
		return null;
	}
}
