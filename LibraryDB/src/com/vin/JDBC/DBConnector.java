package com.vin.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.vin.Error.IssueWarning;
import com.vin.Error.Warning;

/**
 * This class handles exchanges between the database and this Java application.
 * Applications using this class should provide their own interacting class to 
 * access the methods available here. Write a better introductory description once 
 * I have a better idea of what I'm doing
 * 
 * WIP
 * TODO List
 * Set up a methods for the statement object to reduce the number of try/catch statements
 * Figure out how that affects ResultSet(MetaData). Currently there shouldn't be any issue
 * handing data as Strings or primitives, keeping those two in their own respective methods would make
 * reading through this easier
 * 
 * 
 * @author viney
 *
 */
public class DBConnector { //TODO Remove stacktraces once verified
	
	/**
	 * Predefined link
	 */
	private static DBConnector dbc = new DBConnector("jdbc:mysql://localhost:3306/library", "root", "root");
	
	private Connection conn;
	
	/**
	 * DBConnector Constructor
	 * 
	 * @param url - link to database
	 * @param username
	 * @param password
	 */
	private DBConnector(String url, String username, String password) {
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	private DBConnector(String url) {
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	/**
	 * DBConnector singleton should stay within this package, being accessed
	 * through assistant class.
	 * 
	 * @return this DBConnector object
	 */
	protected static DBConnector getConnector() {
		return dbc;
	}
	
	protected String getAllTables() {
		StringBuilder result = new StringBuilder();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SHOW TABLES");
			while (rs.next()) {
				result.append(rs.getString(1) + "\n");
			}
		} catch (SQLException e) {
			IssueWarning.code(Warning.SQL_ERROR);
			e.printStackTrace();}
		return result.toString();
	}
	
	protected String getColumnNamesFor(String table) {
		StringBuilder result = new StringBuilder();
		try {
			Statement stmt = conn.createStatement();
			ResultSetMetaData rsmd = stmt.executeQuery("SELECT * FROM " + table).getMetaData();
			for(int i = 1, col = rsmd.getColumnCount(); i <= col; i++) 
				result.append(rsmd.getColumnName(i) + (i!=col ? "," : ""));
		} catch (SQLException e) {
			IssueWarning.code(Warning.SQL_ERROR);
			e.printStackTrace();
		}
		return result.toString();
	}
	
	/**
	 * Retrieves the data types assigned to each column in the current table
	 * @return String representation of data types
	 */
	protected String getColumnTypes(String table) {
		StringBuilder result = new StringBuilder();
		try {
			Statement stmt = conn.createStatement();
			ResultSetMetaData rsmd =stmt.executeQuery("SELECT * FROM " + table).getMetaData();
			for(int i = 1, col = rsmd.getColumnCount(); i <= col; i++) 
				result.append(rsmd.getColumnTypeName(i) + (i!=col ? "," : ""));
		} catch (SQLException e) {
			IssueWarning.code(Warning.SQL_ERROR);
			e.printStackTrace();
		}
		return result.toString();
	}
	
	private int noOfColumns(String table) {
		try {
			Statement stmt = conn.createStatement();
			ResultSetMetaData rsmd = stmt.executeQuery("SELECT * FROM " + table).getMetaData();
			return rsmd.getColumnCount();
		} catch (SQLException e) {
			IssueWarning.code(Warning.SQL_ERROR);
			e.printStackTrace();
		} return 0;
	}
	
	protected String[] getAllRowsFor(String table) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
			StringBuilder result = new StringBuilder();
			int columns = noOfColumns(table);
			while(rs.next()) {
				for(int i = 1; i <= columns; i++)
					result.append(rs.getString(i) + (i != columns ? "," : "\n"));
			} return result.toString().split("\n");
		} catch (SQLException e) {
			IssueWarning.code(Warning.SQL_ERROR);
			e.printStackTrace();
		} return null;
	}
	
	protected String[] getAllRowsForColumn(String table, String column) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT " + column + " FROM " + table);
			StringBuilder result = new StringBuilder();
			while(rs.next()) {
					result.append(rs.getString(1) + "\n");
			} return result.toString().split("\n");
		} catch (SQLException e) {
			IssueWarning.code(Warning.SQL_ERROR);
			e.printStackTrace();
		} return null;
	}
	
	protected String[] search(String table, String column, String searchFor) {
		if(!getColumnNamesFor(table).contains(column)) return null;
		String sql = "SELECT * FROM " + table + " WHERE " + column + " LIKE '%" + searchFor + "%'";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(sql);
			StringBuilder result = new StringBuilder();
			int columns = noOfColumns(table);
			while(rs.next()) {
				for(int i = 1; i <= columns; i++)
					result.append(rs.getString(i) + (i != columns ? "," : "\n"));
			} return result.toString().split("\n");
		} catch (SQLException e) {
			e.printStackTrace();
		} return null;
	}
	
	protected String[] search(String table, String column1, String key, String column2, String searchFor) {
		if(!(getColumnNamesFor(table).contains(column1) && getColumnNamesFor(table).contains(column2))) return null;
		String sql = "SELECT * FROM " + table + " WHERE " + column1 + " = " + key 
				+ " AND " + column2 + " = " + searchFor;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(sql);
			StringBuilder result = new StringBuilder();
			int columns = noOfColumns(table);
			while(rs.next()) {
				for(int i = 1; i <= columns; i++)
					result.append(rs.getString(i) + (i != columns ? "," : "\n"));
			} return result.toString().split("\n");
		} catch (SQLException e) {
			e.printStackTrace();
		} return null;
	}
	
	protected String[] searchExact(String table, String column, String searchFor) {
		if(!getColumnNamesFor(table).contains(column)) return null;
		String sql = "SELECT * FROM " + table + " WHERE " + column + " = " + searchFor;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(sql);
			StringBuilder result = new StringBuilder();
			int columns = noOfColumns(table);
			while(rs.next()) {
				for(int i = 1; i <= columns; i++)
					result.append(rs.getString(i) + (i != columns ? "," : "\n"));
			} return result.toString().split("\n");
		} catch (SQLException e) {
			e.printStackTrace();
		} return null;
	}
	
	/**
	 * Quick implementation for something I'm not sure how to orchestrate quickly.
	 * 
	 * @param sql
	 * @return
	 */
	protected String[] executeLiteralSearch(String sql) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			StringBuilder result = new StringBuilder();
			int columns = 3;
			while(rs.next()) {
				for(int i = 1; i <= columns; i++)
					result.append(rs.getString(i) + (i != columns ? "," : "\n"));
			} return result.toString().split("\n");
		} catch (SQLException e) {
			e.printStackTrace();
		} return null;
	}
	
	/**
	 * Insert values into the connected database via JDBC.
	 * Assumed safe from providing source.
	 * 
	 * @param table table name in the database
	 * @param values list of values to push
	 * @return true if pushed
	 */
	protected boolean insertRow(String table, String... values) { //TODO revert to boolean, push to database
		if(values.length != noOfColumns(table)) {
			System.out.println("Column Mismatch");
			return false;
		}
		String sql = "INSERT INTO " + table +
				" VALUES(" + Arrays.toString(values).replaceAll("\\[|\\]", "") + ")";
		try {
			Statement stmt = conn.prepareStatement(sql);
			return (stmt.executeUpdate(sql) > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	protected boolean updateRow(String table, String column, String oldValue, String newValue) {
		boolean containsColumn = getColumnNamesFor(table).contains(column);
		if(!containsColumn) return false;
		
		String sql = "UPDATE " + table + " SET " + column + " = '" + newValue
					+ "' WHERE " + column + " = '" + oldValue + "'";
		try {
			Statement stmt = conn.createStatement();
			return (stmt.executeUpdate(sql) > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected boolean updateRowByKey(String table, String column1, int key, String column2, String newValue) {
		boolean containsColumns = getColumnNamesFor(table).contains(column1) 
				&& getColumnNamesFor(table).contains(column2);
		if(!containsColumns) return false;
		
		String sql = "UPDATE " + table + " SET " + column2 + " = '" + newValue
				+ "' WHERE " + column1 + " = '" + key + "'";
		try {
			Statement stmt = conn.createStatement();
			return (stmt.executeUpdate(sql) > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected boolean updateRowBy2Keys(String table, String column1, int key1, String column2, int key2, String column3, String newValue) {
		boolean containsColumns = getColumnNamesFor(table).contains(column1) 
				&& getColumnNamesFor(table).contains(column2)
				&& getColumnNamesFor(table).contains(column3);
		if(!containsColumns) return false;
		
		String sql = "UPDATE " + table + " SET " + column3 + " = " + newValue
				+ " WHERE " + column1 + " = " + key1
				+ " AND " + column2 + " = " + key2;
		try {
			Statement stmt = conn.createStatement();
			return (stmt.executeUpdate(sql) > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Currently not used
	 * 
	 * @param table
	 * @param noOfArgs
	 * @return
	 */
	private String generateSQL(String table, int noOfArgs) {
		StringBuilder sql = new StringBuilder();
		String[] dataTypes = getColumnTypes(table).split(",");
		for(String dataType : dataTypes) {
			sql.append("?,");
		} return sql.substring(0, sql.length()-1);
	}
	
	/**
	 * Expected sql string of deleting
	 * @param sql
	 * @return
	 */
	protected boolean deleteRow(String sql) {
		try {
			Statement stmt = conn.createStatement();
			return (stmt.executeUpdate(sql) > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected boolean deleteRow(String table, String fkey1, String fkey2) {
		
		return true;
	}
	
	//TODO
	/**
	 * In this database we have 3 types, INT, VARCHAR, and DATETIME
	 * 
	 * Formatting instructions needs to ensure that:
	 * INT is inserted as a plain string
	 * VARCHAR is contained within single quotes
	 * DATETIME I'm not sure of, but if I'm lucky the way it was formatted when generated
	 * 	will permit me to just insert as is
	 */
	
	
//	public static void main(String[] args) {
//		String url = "jdbc:mysql://localhost:3306/library";
//		try {
//			Connection conn = DriverManager.getConnection(url, "root", "root");
//			Statement stmt = conn.createStatement();
//			stmt.execute("DELETE FROM tbl_author WHERE authorName = 'DEFAULT'");
//			stmt.execute("INSERT INTO tbl_author values(0, 'DEFAULT')");
//			ResultSet rs = stmt.executeQuery("show tables");
//			
//			while(rs.next()) {
//				System.out.println(rs.getString(1));
//			}
//		} catch (SQLException e) {e.printStackTrace();}
//	}
}
