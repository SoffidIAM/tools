package com.soffid.tools.db.persistence;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.soffid.tools.db.schema.Column;
import com.soffid.tools.db.schema.Database;
import com.soffid.tools.db.schema.ForeignKey;
import com.soffid.tools.db.schema.Index;
import com.soffid.tools.db.schema.Sequence;
import com.soffid.tools.db.schema.Table;

public class XmlReader {
	public Database parse (InputStream in) throws Exception
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();

		Database db = new Database();
		Node root = (Node) xpath.evaluate("/database", doc, XPathConstants.NODE);
		//
		// SEQUENCES
		//
		NodeList nodeSet = (NodeList) xpath.evaluate("sequence", root, XPathConstants.NODESET);
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Node node = nodeSet.item(i);
			
			Sequence seq = new Sequence();
			db.sequences.add(seq);
			seq.name = xpath.evaluate("@name", node);
		}
		//
		// TABLES
		//
		nodeSet = (NodeList) xpath.evaluate("table", root, XPathConstants.NODESET);
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Node node = nodeSet.item(i);
			
			Table t = new Table();
			db.tables.add(t);
			String tableName = xpath.evaluate("@name", node);
			t.name = tableName;
			NodeList columnsSet = (NodeList) xpath.evaluate("column", node, XPathConstants.NODESET);
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Node n = columnsSet.item(j);
				Column c = new Column();
				c.name = xpath.evaluate("@name", n);
				c.type = xpath.evaluate("@type", n);
				c.length = xpath.evaluate("@length", n);
				c.autoIncrement = "true".equals(xpath.evaluate("@autoIncrement", n));
				c.primaryKey = "true".equals(xpath.evaluate("@primaryKey", n));
				c.notNull = c.autoIncrement || c.primaryKey || "true".equals(xpath.evaluate("@notNull", n));
			t.columns.add(c);
			}
		}
		
		//
		// FOREIGN KEYS
		//
			
		nodeSet = (NodeList) xpath.evaluate("foreignKey", root, XPathConstants.NODESET);
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Node node = nodeSet.item(i);
			ForeignKey fk = new ForeignKey();
			db.indexes.add(fk);
			db.foreignKeys.add(fk);
			fk.tableName = xpath.evaluate("@table", node);
			fk.name = xpath.evaluate("@name", node);
			NodeList columnsSet = (NodeList) xpath.evaluate("column", node, XPathConstants.NODESET);
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Node n = columnsSet.item(j);
				fk.columns.add(xpath.evaluate("@name", n));
			}
			fk.foreignTable = xpath.evaluate("@foreignTable", node);
			columnsSet = (NodeList) xpath.evaluate("foreignColumn", node, XPathConstants.NODESET);
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Node n = columnsSet.item(j);
				fk.foreignKeyColumns.add(xpath.evaluate("@name", n));
			}
		}

		//
		// OTHER INDEXES
		//
			
		nodeSet = (NodeList) xpath.evaluate("index", root, XPathConstants.NODESET);
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Node node = nodeSet.item(i);
			Index ndx = new Index();
			db.indexes.add(ndx);
			ndx.tableName = xpath.evaluate("@table", node);
			ndx.name = xpath.evaluate("@name", node);
			ndx.unique = "true".equals(xpath.evaluate("@unique", node));
			NodeList columnsSet = (NodeList) xpath.evaluate("column", node, XPathConstants.NODESET);
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Node n = columnsSet.item(j);
				ndx.columns.add(xpath.evaluate("@name", n));
			}
		}
		return db;
	}

	public void parse (Database db, InputStream in) throws Exception
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();

		Element root = doc.getDocumentElement();
		//
		// SEQUENCES
		//
		NodeList nodeSet = root.getElementsByTagName("sequence");
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Element node = (Element) nodeSet.item(i);
			String name = node.getAttribute("name");
			if ( db.findSequence (name, true) == null)
			{
				Sequence seq = new Sequence();
				db.sequences.add(seq);
				seq.name = name;
			}
		}
		//
		// TABLES
		//
		nodeSet = root.getElementsByTagName("table");
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Element node = (Element) nodeSet.item(i);
			String tableName = node.getAttribute("name");
			
			Table t = db.findTable(tableName, true);
			if (t == null)
			{
				t = new Table();
				db.tables.add(t);
				t.name = tableName;
			}
			NodeList columnsSet = (NodeList) xpath.evaluate("column", node, XPathConstants.NODESET);
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Element n = (Element) columnsSet.item(j);
				String columnName = n.getAttribute("name");
				Column c = t.findColumn (columnName, false);
				if (c == null)
				{
					c = new Column();
					c.name = columnName;
					t.columns.add(c);
				}
				c.type = n.getAttribute("type");
				c.length = n.getAttribute("length");
				c.autoIncrement = "true".equals(n.getAttribute("autoIncrement"));
				c.primaryKey = "true".equals(n.getAttribute("primaryKey"));
				c.notNull = c.autoIncrement || c.primaryKey || "true".equals(n.getAttribute("notNull"));
			}
		}
		
		//
		// FOREIGN KEYS
		//
			
		nodeSet = root.getElementsByTagName("foreignKey");
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Element node = (Element) nodeSet.item(i);
			String tableName = node.getAttribute("table");
			String name = node.getAttribute("name");
			Index idx = db.findIndex(tableName, name, true);
			if (idx != null)
				db.indexes.remove(idx);
			ForeignKey fk = db.findForeignKey(tableName, name, true);
			if (fk != null)
				db.foreignKeys.remove(fk);

			fk = new ForeignKey();
			db.indexes.add(fk);
			db.foreignKeys.add(fk);
			fk.tableName = tableName;
			fk.name = name;
			NodeList columnsSet = node.getElementsByTagName("column");
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Element e = (Element) columnsSet.item(j);
				fk.columns.add(e.getAttribute("name"));
			}
			fk.foreignTable = node.getAttribute("foreignTable");
			columnsSet = node.getElementsByTagName("foreignColumn");
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Element e = (Element) columnsSet.item(j);
				fk.foreignKeyColumns.add(e.getAttribute("name"));
			}
		}

		//
		// OTHER INDEXES
		//
			
		nodeSet = root.getElementsByTagName("index");
		for (int i = 0; i < nodeSet.getLength(); i++)
		{
			Element node = (Element) nodeSet.item(i);
			String tableName = node.getAttribute("table");
			String name = node.getAttribute("name");
			Index idx = db.findIndex(tableName, name, true);
			if (idx != null)
				db.indexes.remove(idx);

			Index fk = new Index();
			db.indexes.add(fk);
			fk.tableName = tableName;
			fk.name = name;
			fk.unique = "true".equals(xpath.evaluate("@unique", node));
			NodeList columnsSet = node.getElementsByTagName("column");
			for (int j = 0; j < columnsSet.getLength(); j++)
			{
				Element e = (Element) columnsSet.item(j);
				fk.columns.add(e.getAttribute("name"));
			}
		}
	}

}
