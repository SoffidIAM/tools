package com.soffid.toold.db.test;

import java.sql.Connection;
import java.sql.DriverManager;

import com.soffid.tools.db.persistence.DbReader;
import com.soffid.tools.db.persistence.XmlReader;
import com.soffid.tools.db.persistence.XmlWriter;
import com.soffid.tools.db.schema.Database;
import com.soffid.tools.db.updater.MsSqlServerUpdater;
import com.soffid.tools.db.updater.MySqlUpdater;
import com.soffid.tools.db.updater.OracleUpdater;
import com.soffid.tools.db.updater.PostgresqlUpdater;

import junit.framework.TestCase;

public class CreateTest extends TestCase {
	public void testCreate () throws Exception
	{
		try {
	        	Class c = Class.forName("com.mysql.jdbc.Driver");
	        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
	        	Connection conn =  DriverManager.getConnection("jdbc:mysql://localhost/soffid", "soffid", "geheim");
			try {
				Database db = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database.xml"));
				new MySqlUpdater().update(conn, db);
		
				Database db2 = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database2.xml"));
				new MySqlUpdater().update(conn, db2);
			} catch (Exception e) {
				e.printStackTrace() ;
				fail ("Exception producted: "+e.toString());
			}
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test MYSQL");
		}
	        
	}

	public void testSqlServer () throws Exception
	{
		try {
	        	Class c = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
	        	Connection conn =  DriverManager.getConnection("jdbc:sqlserver://10.129.123.2:1433;databaseName=soffid", "soffid01", "10Cañonesporbarba");
	        	
			try {
				Database db = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database.xml"));
				new MsSqlServerUpdater().update(conn, db);
		
				Database db2 = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database2.xml"));
				new MsSqlServerUpdater().update(conn, db2);
			} catch (Exception e) {
				e.printStackTrace() ;
				fail ("Exception producted: "+e.toString());
			}
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test SqlServer");
		}
	        
	}

	public void testOracle () throws Exception
	{
		try {
	        	Class c = Class.forName("oracle.jdbc.driver.OracleDriver");
	        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
	        	Connection conn =  DriverManager.getConnection("jdbc:oracle:thin:@oracle.soffid.com:1521:projectnet", "test", "test");
			try {
	        	
				Database db = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database.xml"));
				new OracleUpdater().update(conn, db);
		
				Database db2 = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database2.xml"));
				new OracleUpdater().update(conn, db2);
			} catch (Exception e) {
				e.printStackTrace() ;
				fail ("Exception producted: "+e.toString());
			}
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test Oracle");
		}
	        
	}

	public void testCreate2 () throws Exception
	{
		try {
	        	Class c = Class.forName("com.mysql.jdbc.Driver");
	        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
	        	Connection conn =  DriverManager.getConnection("jdbc:mysql://localhost/soffid", "soffid", "geheim");
			try {
	        	
				Database db = new Database();
				XmlReader r = new XmlReader();
				r.parse(db, CreateTest.class.getResourceAsStream("/core-ddl.xml"));
				r.parse(db, CreateTest.class.getResourceAsStream("/console-ddl.xml"));
				new MySqlUpdater().update(conn, db);
			} catch (Exception e) {
				e.printStackTrace() ;
				fail ("Exception producted: "+e.toString());
			}
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test Oracle");
		}
	        
	}

	public void testPostgresql () throws Exception
	{
		try {
	        	Class c = Class.forName("org.postgresql.Driver");
	        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
	        	Connection conn =  DriverManager.getConnection("jdbc:postgresql://localhost/soffid", "soffid", "soffid");
			try {
	        	
				Database db = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database.xml"));
				new PostgresqlUpdater().update(conn, db);
		
				Database db2 = new XmlReader().parse(CreateTest.class.getResourceAsStream("/database2.xml"));
				new PostgresqlUpdater().update(conn, db2);
			} catch (Exception e) {
				e.printStackTrace() ;
				fail ("Exception producted: "+e.toString());
			}
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test Oracle");
		}
	        
	}
}
