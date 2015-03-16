package com.soffid.tools.db.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.soffid.tools.db.schema.Column;
import com.soffid.tools.db.schema.Database;
import com.soffid.tools.db.schema.ForeignKey;
import com.soffid.tools.db.schema.Index;
import com.soffid.tools.db.schema.Sequence;
import com.soffid.tools.db.schema.Table;

public class XmlWriter {
	private String escapeString (String in)
	{
		return in == null ? null:
			in
			.replaceAll("'", "&apos;")
			.replaceAll("\"", "&quot;")
			.replaceAll(">", "&gt;")
			.replaceAll("<", "&lt;");
	}
	
	public void dump (Database db, OutputStream out) throws Exception
	{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		pw.printf("<?xml version='1.0' encoding='UTF-8'?>\n");
		pw.printf("<database>\n");
		ArrayList<Index> indexToDo = new ArrayList<Index>(db.indexes);
		//
		// SEQUENCES
		//
		pw.printf("<!-- \n  **\n  ** SEQUENCES\n  **\n  -->\n");
		
		for (Sequence seq: db.sequences)
		{
			pw.printf("\t<sequence name='%s'/>\n", escapeString(seq.name));
		}
		
		//
		// TABLES
		//
		pw.printf("\n<!-- \n  **\n  ** TABLES & INDEXES\n  **\n  -->\n\n");
		for (Table t: db.tables)
		{
			pw.printf("\n\t<!-- TABLE %s  -->\n\n", t.name);
			pw.printf ("\t<table name='%s'>\n", escapeString(t.name));
			for (Column c: t.columns)
			{
				pw.printf("\t\t<column name='%s' type='%s'",
						escapeString(c.name), escapeString(c.type)); 
				if (c.length != null && !c.length.isEmpty())
					pw.printf(" length='%s'", escapeString(c.length));
				if (c.autoIncrement)
					pw.printf(" autoIncrement='true'");
				if (c.notNull)
					pw.printf(" notNull='true'");
				if (c.primaryKey)
					pw.printf (" primaryKey='true'");
				pw.printf ("/>\n");
				
			}
			
			pw.printf ("\t</table>\n\n");

			//
			// FOREIGN KEYS
			//
			for (Iterator<Index> it = indexToDo.iterator(); it.hasNext(); )
			{
				Index idx = it.next();
				if (idx instanceof ForeignKey && idx.tableName.equals(t.name))
				{
					it.remove();
					ForeignKey fk = (ForeignKey) idx;
					pw.printf ("\t<foreignKey name='%s' table='%s' foreignTable='%s'>\n",
							escapeString(idx.name), escapeString(idx.tableName),
							escapeString(fk.foreignTable));
					for (String column: idx.columns)
					{
						pw.printf ("\t\t<column name='%s'/>\n", escapeString(column));
					}
					for (String column: fk.foreignKeyColumns)
					{
						pw.printf ("\t\t<foreignColumn name='%s'/>\n", escapeString(column));
					}
					pw.printf ("\t</foreignKey>\n\n");
				}
			}

					

			//
			// INDEXES
			//
			for (Iterator<Index> it = indexToDo.iterator(); it.hasNext(); )
			{
				Index idx = it.next();
				if (idx.tableName.equals(t.name))
				{
					it.remove();
					pw.printf ("\t<index name='%s' table='%s' unique='%s'>\n",
							escapeString(idx.name), escapeString(idx.tableName),
							idx.unique?"true": "false");
					for (String column: idx.columns)
					{
						pw.printf ("\t\t<column name='%s'/>\n", escapeString(column));
					}
					pw.printf ("\t</index>\n\n");
				}
			}
		}
		pw.printf("</database>\n");
		pw.close ();
	}
}
