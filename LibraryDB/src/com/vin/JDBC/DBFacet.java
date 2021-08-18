package com.vin.JDBC;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vin.Error.IssueWarning;
import com.vin.Error.Warning;

/**
 * Interface to class to interact with JDBC connection.
 * 
 * 
 * 
 *  
 * @author viney
 *
 */
public class DBFacet {
	
	private DBConnector dbc;
	private String currentTable;
	
	public static void main(String[] args) {
		DBFacet test = new DBFacet();
//		dbc.setTable("tbl_book");
//		System.out.println(dbc.getAllTables());
//		dbc.setTable("tbl_book");
//		System.out.println(dbc.getColumnNamesFor());
//		System.out.println(dbc.getColumnTypes());
//		dbc.setTable("tbl_book_copies");
//		System.out.println(dbc.getColumnNamesFor());
//		System.out.println(dbc.getColumnTypes());
//		dbc.setTable("tbl_book");
//		System.out.println(dbc.insertValues("123", "123", "1234", "2020-20-30 14:20:30", "2021-20-30 14:20:30"));
		
//		test.setTable("tbl_publisher");
//		test.pushSpreadsheet("temp/tbl_publisher.csv");
//		
//		test.setTable("tbl_author");
//		test.pushSpreadsheet("temp/tbl_author.csv");
//		
//		test.setTable("tbl_book");
//		test.pushSpreadsheet("temp/tbl_book.csv");
//		
//		test.setTable("tbl_borrower");
//		test.pushSpreadsheet("temp/tbl_borrower.csv");
//		
//		test.setTable("tbl_library_branch");
//		test.pushSpreadsheet("temp/tbl_library_branch.csv");
//		
//		test.setTable("tbl_book_loans");
//		test.pushSpreadsheet("temp/tbl_book_loans.csv");
//		
//		test.setTable("tbl_book_copies");
//		test.pushSpreadsheet("temp/tbl_book_copies.csv");
//		test.setTable("tbl_library_branch");
//		System.out.println(test.search("branchName", "County"));
//		test.setTable("tbl_library_branch");
//		Arrays.stream(test.getTableData()).forEach(s -> System.out.println(s));
//		System.out.println(test.getTableData().length);
//		Insert into tbl_book_loans 
//		values(205746, 13, 889, '2021-05-10 12:31:05', '2021-08-10 18:00:00');

	}
	
	
	public DBFacet() {
		dbc = DBConnector.getConnector();
	}
	
	public DBFacet(String table) {
		this();
		currentTable = table;
	}
		
	/**
	 * Set the current working table to be accessed for subsequent queries.
	 * 
	 * @param table
	 */
	public void setTable(String table) {
		currentTable = table;
	}
	
	/**
	 * Gets all data in this table while leaving the content in csv format.
	 * 
	 * @return
	 */
	public String[] getTableData() {
		return dbc.getAllRowsFor(currentTable);
	}
	
	/**
	 * Returns all values listed in this column throughout the table.
	 * 
	 * @param column
	 * @return
	 */
	public String[] getTableDataForColumn(String column) {
		return dbc.getAllRowsForColumn(currentTable, column);
	}
	
	/**
	 * Search for all rows containing a similar value in the
	 * specified column.
	 * 
	 * @param column
	 * @param searchQuery
	 * @return
	 */
	public String[] search(String column, String searchQuery) {
		//do something here to prevent sql injection
		if(searchQuery.contains(";")) {
			IssueWarning.code(Warning.BAD_INPUT);
			return null;
		}
		return dbc.search(currentTable, column, searchQuery);
	}
	
	/**
	 * Searches for a query regarding a specific key
	 * 
	 * @param column1
	 * @param key
	 * @param column2
	 * @param searchQuery
	 * @return
	 */
	public String[] search(String column1, String key, String column2, String searchQuery) {
		if(searchQuery.contains(";")) {
			IssueWarning.code(Warning.BAD_INPUT);
			return null;
		} return dbc.search(currentTable, column1, key, column2, searchQuery);
	}
	
	/**
	 * Searches for a literal expression in the column described
	 * @param column
	 * @param key
	 * @return
	 */
	public String[] searchExact(String column, String key) {
		return dbc.searchExact(currentTable, column, key);
	}


	public boolean pushRequest(String... data) {
		return dbc.insertRow(currentTable, data);
	}

	/**
	 * Push a modification to an existing row and value.
	 * 
	 * @param column
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	public boolean changeRequest(String column, String oldValue, String newValue) {
		
		return dbc.updateRow(currentTable, column, oldValue, newValue);
	}
	
	/**
	 * Update a row by key
	 * 
	 * @param table
	 * @param column1
	 * @param pKey
	 * @param column2
	 * @param newValue
	 * @return
	 */
	public boolean changeRequest(String column1, int key, String column2, String newValue) {
		return dbc.updateRowByKey(currentTable, column1, key, column2, newValue);
	}
	
	/**
	 * Update a row by 2 keys
	 * 
	 * @param column1
	 * @param key
	 * @param column2
	 * @param key
	 * @param column3
	 * @param newValue
	 * @return
	 */
	public boolean changeRequest(String column1, int key1, String column2, int key2, String column3, String newValue) {
		return dbc.updateRowBy2Keys(currentTable, column1, key1, column2, key2, column3, newValue);
	}

	/**
	 * Attempt to remove a row from the database.
	 * 
	 * Currently hard codes the sequence for deleting a loan
	 * via {@code Borrower.checkIn}
	 * 
	 * @return TRUE if row was removed
	 */
	public boolean pullRequest(int primaryKey, int fkey1, int fkey2) {
		return dbc.deleteRow("DELETE FROM tbl_book_loans "
				+ "WHERE cardNo = " + primaryKey + " "
				+ "AND branchId = " + fkey1 + " "
				+ "AND bookId = " + fkey2);
	}
	
	public String[] timeNowTimeDue(int months) {
		String[] result = new String[2];
		DateTimeFormatter out = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter due = DateTimeFormatter.ofPattern("yyyy-MM-dd 18:00:00");
		LocalDateTime now = LocalDateTime.now();
		result[0] = out.format(LocalDateTime.now());
		result[1] = due.format(now.plusMonths(months));
		
		return result;
	}
	
	/**
	 * SELECT tbl_book.bookId, tbl_book.title, tbl_book_copies.noOfCopies
		From tbl_book_copies
		INNER JOIN tbl_book ON tbl_book_copies.bookId = tbl_book.bookId
		WHERE tbl_book_copies.branchId = 13;
		
		Because I lack the stamina to think of a way to push this, I'm making a literal method.
		Will revisit if I have more time later.
		
		
		
		@return An array of CSV style data regarding the joining of two tables in the database
			linked on a key.
			
	 */
	public String[] searchAndJoinBooksAndCopies(String branchId) {
		return dbc.executeLiteralSearch("SELECT tbl_book.bookId, tbl_book.title, tbl_book_copies.noOfCopies "
				+ "FROM tbl_book_copies INNER JOIN tbl_book ON tbl_book_copies.bookId = tbl_book.bookId "
				+ "WHERE tbl_book_copies.branchId = " + branchId);
	}
	
	/**
	 * SELECT tbl_book.bookId, tbl_book.title, tbl_book_loans.dueDate
		From tbl_book_loans
		INNER JOIN tbl_book ON tbl_book_loans.bookId=tbl_book.bookId
		where cardNo = 889
		and branchId = 13;
	 * @param branchId
	 * @param cardNo
	 * @return
	 */
	public String[] getLoanedBooks(String branchId, String cardNo) {
		return dbc.executeLiteralSearch("SELECT tbl_book.bookId, tbl_book.title, tbl_book_loans.dueDate "
				+ "FROM tbl_book_loans "
				+ "INNER JOIN tbl_book ON tbl_book_loans.bookId=tbl_book.bookId "
				+ "WHERE cardNo = " + cardNo + " "
				+ "AND branchId = " + branchId);
	}
	
	public boolean adminDeleteByKey(String column, String pKey) {
		return dbc.deleteRow("DELETE FROM " + currentTable + " WHERE " + column + " = " + pKey);
	}
	
	public String[] viewBookStockFor(String branchId) {
		
	}
	
	/**
	 * Attempts to push a spreadsheet of values to the current table.
	 * Values should be formatted CSV, with correct data types and headers 
	 * for the table being pushed to.
	 * 
	 * @param tableSpreadSheet File location of values to push
	 */
	private void pushSpreadsheet(String tableSpreadSheet) {
		try(BufferedReader br = new BufferedReader(new FileReader(tableSpreadSheet))) {
			if(!br.readLine().equals(dbc.getAllTables())) {
				IssueWarning.code(Warning.INCORRECT_ARGUMENTS);
				//return;
			}
			String data = null;
			while((data = br.readLine()) != null) {
				dbc.insertRow(currentTable, data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("All values pushed.");
	}


	public boolean pullRequest(int primaryKey) {
		return false;
	}
}
