package com.vin.DataManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Separate application for forming table data.
 * 
 * @author viney
 *
 */
public class SQLTableGeneratorApp {
	public static void main(String[] args) {
		SQLTableGeneratorApp makeTables = new SQLTableGeneratorApp();
//		makeTables.generateRandomDates();
//		makeTables.scrapeLib();
//		makeTables.noOfCopies(); //This doesn't work
		SQLTableGenerator makeFullTables = new SQLTableGenerator();
		
//		String[] fileList = 
//			{"temp/book_copies.csv",
//					"temp/book_data.csv",
//					"temp/due_dates.csv",
//					"temp/library_data.csv",
//					"temp/tbl_author.csv",
//					"temp/tbl_borrower.csv",
//					"temp/tbl_publisher.csv",
//					"temp/WestVirginiaLibraries.csv"};
//		
//		{ 	/**Import all data needed*/
//			makeFullTables.importValues(fileList);
//		}	
//			
//		{	/**tbl_book_copies*/
//			makeFullTables.setRowCount(1000);
//		
//			makeFullTables.pushToOutSet("temp/book_data.csv", "bookId");
//			makeFullTables.pushToOutSet("temp/library_data.csv", "branchId");
//			makeFullTables.pushToOutSet("temp/book_copies.csv", "noOfCopies");
//		
//			makeFullTables.exportNewTable("temp/tbl_book_copies.csv");
//		} makeFullTables.clearOutSet();
//		
//		{	/**tbl_book_loans*/
//			makeFullTables.setRowCount(700);
//			
//			makeFullTables.pushToOutSet("temp/book_data.csv", "bookId");
//			makeFullTables.pushToOutSet("temp/library_data.csv", "branchId");
//			makeFullTables.pushToOutSet("temp/tbl_borrower.csv", "cardNo");
//			makeFullTables.pushToOutSet("temp/due_dates.csv", "dateOut");
//			makeFullTables.pushToOutSet("temp/due_dates.csv", "dueDate");
//			
//			makeFullTables.exportNewTable("temp/tbl_book_loans.csv");
//		} makeFullTables.clearOutSet();
//		
//		{	/**tbl_library_branch*/
//			makeFullTables.setRowCount(91);
//			
//			makeFullTables.pushToOutSet("temp/library_data.csv", "branchId");
//			makeFullTables.pushToOutSet("temp/WestVirginiaLibraries.csv", "branchName");
//			makeFullTables.pushToOutSet("temp/library_data.csv", "branchAddress");
//			
//			makeFullTables.exportNewTable("temp/tbl_library_branch.csv");
//		} makeFullTables.clearOutSet();
//		
//		{	/**tbl_book*/
//			makeFullTables.setRowCount(1000);
//			
//			makeFullTables.pushToOutSet("temp/book_data.csv", "bookId");
//			makeFullTables.pushToOutSet("temp/book_data.csv", "title");
//			makeFullTables.pushToOutSet("temp/tbl_author.csv", "authorId");
//			makeFullTables.pushToOutSet("temp/tbl_publisher.csv", "publisherId");
//			
//			makeFullTables.exportNewTable("temp/tbl_book.csv");
//		} makeFullTables.clearOutSet();
		
		{	/**tbl_publisher because I forgot to generate phone numbers*/
			makeFullTables.importValues("temp/oldFiles/tbl_publisher_old.csv");
			makeFullTables.importValues("temp/oldFiles/publisher_phone.csv");
			
			makeFullTables.setRowCount(45);
			
			makeFullTables.pushToOutSet("temp/oldFiles/tbl_publisher_old.csv", "publisherId");
			makeFullTables.pushToOutSet("temp/oldFiles/tbl_publisher_old.csv", "publisherName");
			makeFullTables.pushToOutSet("temp/oldFiles/tbl_publisher_old.csv", "publisherAddress");
			makeFullTables.pushToOutSet("temp/oldFiles/publisher_phone.csv", "publisherPhone");
			
			makeFullTables.exportNewTable("temp/tbl_publisher.csv");
		}
	}
	
	private void noOfCopies() {
		StringBuilder books = new StringBuilder();
		books.append("noOfCopies\n");
		for(int i = 0; i < 700; i++) {
			books.append(Integer.toString((int)(Math.random()*12)) + "\n");
		} System.out.println(books.toString());
		try {
			File output = new File("temp/book_copies.csv");
			output.createNewFile();
			PrintWriter pw = new PrintWriter(output);
			pw.print(books.toString());
			pw.close();
		} catch(IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Generates a randomized set of dates in SQL_DATETIME format in
	 * a manner that makes sense.
	 * 
	 * Does not consider holidays.
	 */
	private void generateRandomDates() {
		StringBuilder dates = new StringBuilder();
		for(int i = 0; i < 700; i++) {
			boolean overdueChance = (int)(Math.random()*100) == 1 ? true : false;
			int year = (overdueChance) ? 2020 : 2021;
			int month = (int)(1 + Math.random()*5);
			int day = (int)(1 + Math.random()*28);
			
			int hour = (int)(8 + Math.random()*10);
			int minute = (int)(Math.random()*61);
			int second = (int)(Math.random()*61);
			
			int dueIn = (int)(1 + Math.random()*4);
			
			dates.append(year + "-" + String.format("%02d", month) + "-" + String.format("%02d ", day) +
					String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second) + ",");
			dates.append(year + "-" + String.format("%02d", month+dueIn) + "-" + String.format("%02d ", day) +
					"18" + ":" + "00" + ":" + "00" + "\n");
		}
		
		try {
			File output = new File("temp/due_dates.csv");
			output.createNewFile();
			PrintWriter pw = new PrintWriter(output);
			
			pw.write(dates.toString().toCharArray());
			pw.close();
			System.out.println("Complete");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets library names from the library technology website.
	 * 91 total results for West Virginia
	 */
	private void scrapeLib() {
		ArrayList<String> libStore = new ArrayList<>();
		libStore.add("branchName");
		try {
			URL lib = new URL("https://librarytechnology.org/libraries/public.pl?State=West%20Virginia"); //https://librarytechnology.org/libraries/public.pl?State=West%20Virginia
			
			try(BufferedReader br = new BufferedReader(new InputStreamReader(lib.openStream()))) {
				
				String line = null;
				while((line = br.readLine()) != null) {
					if(!line.contains("Library")) continue;
					String libName = line.replaceAll("<(.*)>", "").replaceAll("&nbsp;", "");
					if(libName.length() == 0 || libName.contains("=")) continue;
					libStore.add(libName);
					
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		File output = new File("temp/WestVirginiaLibraries.csv");
		try {
			output.createNewFile();
			PrintWriter pw = new PrintWriter(output);
			for(String lib : libStore) pw.print(lib.trim() + "\n");
			pw.close();
			System.out.println("Complete : " + (libStore.size()-1) + " entries");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
