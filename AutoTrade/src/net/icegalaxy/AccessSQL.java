package net.icegalaxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.sqlite.SQLiteConfig;

public class AccessSQL {

	public AccessSQL(String DBName) {
		this.DBName = DBName;
		try {
			connectToAccessDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void connectToAccessDB() throws SQLException {

//		try {
//			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
//			conn = DriverManager.getConnection("jdbc:odbc:" + DBName);
//			stmt = conn.createStatement();
//			System.out.println("Connected to Database: " + DBName);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		SQLiteConfig config = new SQLiteConfig(); 
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
    
     
        conn = DriverManager.getConnection("jdbc:sqlite:" + DBName + ".sqlite");
        stmt = conn.createStatement();
		
	}

	public void createTable(String tableName,
			String fieldsAndTypesSeparatedByComma) {

		this.tableName = tableName;
		this.fieldString = fieldsAndTypesSeparatedByComma;

		String query = "create table " + tableName + "(" + fieldString + ")";

		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Fail to create table");
			System.out.println(query);
			e.printStackTrace();
			return;
		}

		System.out.println("Created Table: " + tableName);
	}

	public void insertData(String valuesSeparatedByComma) {

		ArrayList<String> values = new ArrayList<String>();
		Scanner scr = new Scanner(valuesSeparatedByComma);
		scr.useDelimiter(",");
		while (scr.hasNext()){
			values.add(scr.next().trim());
		}
		
		StringBuffer query = new StringBuffer("INSERT INTO " + this.tableName + " VALUES(");
			
		for (int i=0; i<values.size(); i++){
			
			if (i != values.size()-1)
				query.append(quote(values.get(i))+ ",");
			else
				query.append(quote(values.get(i)) + ");");			
		}
		
		try {
			stmt.executeUpdate(query.toString());
		} catch (SQLException e) {
			System.out.println("Fail to insert data");
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			stmt.close();
			conn.close();
			System.out.println("Disconnected From DataBase:" + DBName);
		} catch (SQLException e) {
			System.out.println("Fail to Dissconnect");
			e.printStackTrace();
		}
	}
	
	
	private String quote(String point) {
		return ("'" + point + "'");
	}

	private String DBName;
	private String tableName;
	private String fieldString;
	private Statement stmt;
	private Connection conn;

}
