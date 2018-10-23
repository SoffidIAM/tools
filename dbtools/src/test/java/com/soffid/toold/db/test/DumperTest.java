package com.soffid.toold.db.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.soffid.tools.db.persistence.DbReader;
import com.soffid.tools.db.persistence.XmlWriter;
import com.soffid.tools.db.schema.Database;

public class DumperTest extends TestCase {

	public void test() throws Exception {
        	Class c = Class.forName("com.mysql.jdbc.Driver");
        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
        	Connection conn =  null;
		try {
        		conn =  DriverManager.getConnection("jdbc:mysql://localhost/soffid", "soffid", "geheim");
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test MYSQL");
			return;
		}
        
        	Database db =  new DbReader().parse(conn, null);
        	System.out.println ("Dumping database");
        	new XmlWriter().dump(db, System.out);

	}

	private void notestSqlServer() throws Exception {
        	Class c = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	DriverManager.registerDriver((java.sql.Driver) c.newInstance());
        	Connection conn = null;
		try {
			conn =  DriverManager.getConnection("jdbc:sqlserver://10.129.123.2:1433;databaseName=soffid", "soffid01", "10Cañonesporbarba");
		} catch (Exception e) {
			System.out.println ("Error connecting to mysql. Cannot test MYSQL");
			return;
		}
        
        	Database db =  new DbReader().parse(conn, null);
        	System.out.println ("Dumping database");
        	new XmlWriter().dump(db, System.out);

	}
}
