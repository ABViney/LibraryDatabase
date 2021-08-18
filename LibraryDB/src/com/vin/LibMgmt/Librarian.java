package com.vin.LibMgmt;
import java.util.Arrays;
import java.util.Scanner;

import com.vin.Error.IssueWarning;
import com.vin.Error.Warning;
import com.vin.JDBC.DBFacet;


/**
 * 
 * @author viney
 *
 */
public class Librarian {
	
	/**
	 * Table access permitted for Librarians. Used for reference.
	 * 
	 * tbl_library_branch
	 * tbl_book_copies
	 */
	
	private DBFacet dbf;
	private String thisBranchId;
	private String thisBranchName;
	
	/**
	 * Sets default branch to tbl_library_branch upon initialization.
	 */
	public Librarian() {
		dbf = new DBFacet("tbl_library_branch");
	}
	
	/**
	 * First display when a user visits this class, or returns from a later
	 * menu.
	 * 
	 * @param in
	 */
	public void intro(Scanner in) {
		thisBranchId = null;
		thisBranchName = null;
		
		SUBLOOP:
		while(true) {
			System.out.println("1. Enter Branch you manage\n" 
					+ "0. Exit\n");

			switch (Application.getIndex(in, 0, 1, false)) {
			case 0:
				break SUBLOOP;
			case 1:
				introToGetLocation(in);
				break;
			}
		}
	}

	/**
	 * Intermediary between intro and getLocation to determine whether the user wants
	 * to search for their library.
	 * @param in
	 */
	private void introToGetLocation(Scanner in) {
		System.out.println("Enter your branch's name/ID, or press ENTER to browse the directory.");
		Application.fixTheScanner(in);
		String firstComment = in.nextLine().trim();
		if(firstComment.length() > 0) {
			try {
				int iD = Integer.parseInt(firstComment);
				getLocationWithSearch(in, "branchId", Integer.toString(iD));
			} catch (NumberFormatException e) {
				getLocationWithSearch(in, "branchName", firstComment);
			}
		}
		else getLocation(in, dbf.getTableData(), 1);
	}
	
	/**
	 * Default method of selecting a branch. Displays a set of results from the data pulled from the DBC interface.
	 * Increments listing based on user input, sets the user's ID and branch name before moving on.
	 * 
	 * @param in
	 * @param results String array of results from previous query
	 * @param columnsToDisplay varArgs of columns to display. [0, Branch ID : 1, Branch Name] Branch address excluded
	 * 	Branch ID and Branch Name should be printed at the same time, if ID is needed.
	 */
	private void getLocation(Scanner in, String[] results, int... columnsToDisplay) {
		//TODO create a method to display 7 library names at a time -- Coincidentally lines up with 91 libraries. Must be mutable for searched entries though
		 //TODO Finish writing this. Need to display 7 or less entries made from getting results, and then have the case track which one is which. Prevent outofboundsex
		thisBranchName = null;
		thisBranchId = null;
		
		while(true) {
			String[] choice = Application.pickFromList(in, results, columnsToDisplay);
			if(choice == null) break;
			else {
				thisBranchId = choice[0];
				thisBranchName = choice[1];
				break;
			}
			//TODO Ensure encapsulated method is without fault. If it turns out better than i could want, clean up comments
//			int listingsShown = Application.showPartOfList(results, currentIndex, 7, columnsToDisplay);
//			System.out.println();
//			if(currentIndex > 0) System.out.println("8. Previous");
//			if(currentIndex < results.length-7) System.out.println("9. Next");
//			System.out.println("0. Return to previous.");
//			
//			selection = Application.getIndex(in, 0, listingsShown, true);
//			if(selection <= 7) {
//				if(selection == 0) break SUBLOOP;
//				else if(selection+currentIndex-1 < results.length) {
//					thisBranchName = results[selection+currentIndex-1].split(",")[1];
//					thisBranchId = results[selection+currentIndex-1].split(",")[0];
//				}
//			}
//			else if(selection == 8 && currentIndex > 0) {
//				currentIndex -= 7;
//				continue;
//			}
//			else if(selection == 9 && currentIndex < results.length-7) {
//				currentIndex += 7;
//				continue;
//			}
		}
		if(thisBranchName != null) viewLibrary(in);
	}
	
	/**
	 * Method should display rows that match search, permit return to getLocation
	 * 
	 * 
	 * 
	 * @param in
	 * @param searchType column name to scrape
	 * @param searchTerm user input to search 
	 */
	private void getLocationWithSearch(Scanner in, String searchType, String searchTerm) {
		if(searchType.equals("branchId")) {
			getLocation(in, dbf.search(searchType, searchTerm), 0, 1);
		}
		if(searchType.equals("branchName")) {
			getLocation(in, dbf.search(searchType, searchTerm), 1);
		}
	}
	
	
	
	
	private void printBranchInfo() {
		System.out.println("\tBranch: " + thisBranchName + "\tID: " + thisBranchId);
	}
	
	private void viewLibrary(Scanner in) {
		SUBLOOP:
		while (true) {
			printBranchInfo();
			System.out.println("1. Update details of the library.\n" 
					+ "2. Add copies of book to the branch.\n"
					+ "3. View stock for this branch\n"
					+ "0. Exit\n");

			switch (Application.getIndex(in, 0, 2, false)) {
			case 0:
				thisBranchName = null;
				thisBranchId = null;
				break SUBLOOP;
			case 1:
				updateInfo(in);
				break;
			case 2:
				viewLibraryToAddBooks(in);
				break;
			case 3:
				viewLibraryStock(in);
			}
		}
	}

	private void updateInfo(Scanner in) {
		SUBLOOP:
		while (true) {
			printBranchInfo();
			System.out.println("1. Change branch name\n" 
					+ "2. Change branch address\n" 
					+ "0. Return to previous\n");

			switch (Application.getIndex(in, 0, 2, false)) {
			case 0:
				break SUBLOOP;
			case 1:
				newBranchName(in);
				break;
			case 2:
				newBranchAddress(in);
				break;
			}
		}
	}

	private void newBranchAddress(Scanner in) {
		printBranchInfo();
		Application.fixTheScanner(in);
		System.out.print("Enter a new branch address: ");
		String newBranchAddress = in.nextLine().trim();
		System.out.println("You entered: \n\t" + newBranchAddress
				+ "\n\nSave changes?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		boolean SAVE = false;
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			SAVE = true;
			break;
		default:
			SAVE = false;
		}
		
		if(SAVE) {
			if(dbf.changeRequest("branchId", Integer.parseInt(thisBranchId), "branchAddress", newBranchAddress)) {
				System.out.println("Changes successfully made.");
			} else IssueWarning.code(Warning.SQL_ERROR);
			
		} else {
			System.out.println("Changes discarded.");
		}
	}

	private void newBranchName(Scanner in) {
		printBranchInfo();
		Application.fixTheScanner(in);
		System.out.print("Enter a new branch name: ");
		String newBranchName = in.nextLine().trim();
		System.out.println("You entered: " + newBranchName
				+ "\n\nSave changes?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		boolean SAVE = false;
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			SAVE = true;
			break;
		default:
			SAVE = false;
		}
		
		if(SAVE) {
			if(dbf.changeRequest("branchName", thisBranchName, newBranchName)) {
				System.out.println("Changes successfully made.");
				thisBranchName = newBranchName;
			} else IssueWarning.code(Warning.SQL_ERROR);
			
		} else {
			System.out.println("Changes discarded.");
		}
	}

	private void viewLibraryToAddBooks(Scanner in) {
		dbf.setTable("tbl_book");
		System.out.println("Enter the book's name or ID, or press ENTER to browse the directory.");
		Application.fixTheScanner(in);
		String firstComment = in.nextLine().trim();
		if(firstComment.length() > 0) {
			try {
				int iD = Integer.parseInt(firstComment);
				getBookWithSearch(in, "bookId", Integer.toString(iD));
			} catch (NumberFormatException e) {
				getBookWithSearch(in, "title", firstComment);
			}
		}
		else addBooks(in, dbf.getTableData(), 0, 1);
	}
	
	private void getBookWithSearch(Scanner in, String searchType, String searchTerm) {
		if(searchType.equals("bookId")) {
			addBooks(in, dbf.search(searchType, searchTerm), 0, 1);
		}
		if(searchType.equals("title")) {
			addBooks(in, dbf.search(searchType, searchTerm), 0, 1);
		}
	}
	
	/**
	 * Select a book from the recorded list to adjust this branch's list.
	 * Prompts for bookId/bookName, or allows the user to browse the collection of books.
	 * After selection is made, user is prompted to enter the no. of books the library has.
	 * @param in
	 */
	private void addBooks(Scanner in, String[] results, int... columns) {
		while(true) {
			String[] choice = Application.pickFromList(in, results, columns);
			if(choice == null) break;
			else {
				changeStock(in, choice[0], choice[1]);
			}
//			int listingsShown = Application.showPartOfList(results, currentIndex, 7, columns);
//			System.out.println();
//			if(currentIndex > 0) System.out.println("8. Previous");
//			if(currentIndex < results.length-7) System.out.println("9. Next");
//			System.out.println("0. Exit");
//			
//			selection = Application.getIndex(in, 0, listingsShown, true);
//			if(selection <= 7) {
//				if(selection == 0) stillUsing = false;
//				else if(selection+currentIndex-1 < results.length) {
//					book = results[selection+currentIndex-1].split(",")[1];
//					bookId = results[selection+currentIndex-1].split(",")[0];
//					changeStock(in, bookId, book);
//				}
//			}
//			else if(selection == 8 && currentIndex > 0) {
//				currentIndex -= 7;
//				continue;
//			}
//			else if(selection == 9 && currentIndex < results.length-7) {
//				currentIndex += 7;
//				continue;
//			}
		} dbf.setTable("tbl_library_branch");
	}

	/**
	 * Change the stock of a book at a branch.
	 * 
	 * @param in
	 * @param bookId
	 * @param book
	 */
	private void changeStock(Scanner in, String bookId, String book) {
		dbf.setTable("tbl_book_copies");
		String[] currentStock = dbf.search("branchId", thisBranchId, "bookId", bookId)[0].split(",");
		
		int copyCount = 0;
		if(currentStock.length == 3) copyCount = Integer.parseInt(currentStock[2]);
		System.out.println("This branch currently has " + copyCount + (copyCount == 1 ? " copy " : " copies ")
				+ "of this book on file. Would you like to change that?\n");
		
		System.out.println("1. Yes\n"
					+ "2. No\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			break;
		case 2:
			return;
		}
		SUBLOOP:
		while(true) {
			System.out.print("Enter a new count for [" + book + "] : ");
			copyCount = Application.getIndex(in, 0, Integer.MAX_VALUE, false);
			System.out.println("\nYou entered " + copyCount);
			System.out.println("Save changes?\n" 
					+ "1. Yes\n"
					+ "2. No\n");
			boolean SAVE = false;
			switch(Application.getIndex(in, 1, 2, false)) {
			case 1:
				SAVE = true;
				break;
			case 2:
				System.out.println("Would you like to re-enter the count, or exit?\n"
						+ "1. Re-enter value\n"
						+ "2. Exit prompt\n");
				switch(Application.getIndex(in, 1, 2, false)) {
				case 1:
					break;
				case 2:
					break SUBLOOP;
				}
				break;
			}
			if(SAVE) {
				if(currentStock.length != 3) {
					if(dbf.pushRequest(bookId + "," + thisBranchId + "," + copyCount)) {
						System.out.println("Changes successfully saved.");
					} else IssueWarning.code(Warning.SQL_ERROR);
				} else if(dbf.changeRequest("branchId", Integer.parseInt(thisBranchId), "bookId", Integer.parseInt(bookId), "noOfCopies", Integer.toString(copyCount))) {
					System.out.println("Changes successfully saved.");
				} else IssueWarning.code(Warning.SQL_ERROR);
				break SUBLOOP;
			}
		}
	}
	
	private void viewLibraryStock(Scanner in) {
		dbf.setTable("tbl_book_copies");
		String[] currentStock = dbf.searchAndJoinBooksAndCopies(thisBranchId);
		
	}
	
}
