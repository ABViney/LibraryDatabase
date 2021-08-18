package com.vin.LibMgmt;

import java.util.Arrays;
import java.util.Scanner;

import com.vin.Error.IssueWarning;
import com.vin.Error.Warning;
import com.vin.JDBC.DBFacet;

public class Borrower {
	
	private String cardNo;
	private String fullName;
	
	private String currentBranch;
	private String currentBranchId;
	
	DBFacet dbf;
	
	public Borrower() {
		cardNo = null;
		fullName = null;
		currentBranch = null;
		dbf = new DBFacet("tbl_borrower");
	}
	
	public void verifyID(Scanner in) {
		SUBLOOP:
		while(true) {
			Application.fixTheScanner(in);
			System.out.print("Please input your card number: ");
			String attempt = in.nextLine().trim();
			String[] result = dbf.searchExact("cardNo", attempt)[0].split(",");
			if (result[0].equals(attempt)) {
				System.out.println("Are you " + result[1] + "?\n" 
						+ "1. Yes\n" 
						+ "2. No\n");
				switch (Application.getIndex(in, 1, 2, false)) {
				case 1:
					System.out.println("Welcome back!");
					cardNo = result[0];
					fullName = result[1];
					selectBranch(in);
					break SUBLOOP;
				case 2:
					System.out.println("1. Retry\n" + "2. Exit\n");
					switch (Application.getIndex(in, 1, 2, false)) {
					case 1:
						break;
					case 2:
						return;
					}
				}
			}
		} intro(in);
	}
	
	private void selectBranch(Scanner in) {
		dbf.setTable("tbl_library_branch");
		System.out.println("Please enter the branch are you currently visiting: ");
		Application.fixTheScanner(in);
		String branchEstimate = in.nextLine().trim();
		
		String[] results = dbf.search("branchName", branchEstimate);
		int currentIndex = 0;
		int selection;
		
		SUBLOOP:
		while(true) {
			int listingsShown = Application.showPartOfList(results, currentIndex, 7, 1, 2);
			System.out.println();
			if(currentIndex > 0) System.out.println("8. Previous");
			if(currentIndex < results.length-7) System.out.println("9. Next");
			System.out.println("0. Exit");
			
			selection = Application.getIndex(in, 0, listingsShown, true);
			if(selection <= 7) {
				if(selection == 0) break SUBLOOP;
				else if(selection+currentIndex-1 < results.length) {
					currentBranch = results[selection+currentIndex-1].split(",")[1];
					currentBranchId = results[selection+currentIndex-1].split(",")[0];
					break SUBLOOP;
				}
			}
			else if(selection == 8 && currentIndex > 0) {
				currentIndex -= 7;
				continue;
			}
			else if(selection == 9 && currentIndex < results.length-7) {
				currentIndex += 7;
				continue;
			}
		} if(currentBranch != null) intro(in);
	}
	
	public void intro(Scanner in) {
		SUBLOOP:
		while(currentBranch != null) {
			dbf.setTable("tbl_borrower");
			System.out.println("What would you like to do?\n"
					+ "1. Check out a book\n"
					+ "2. Return a book\n"
					+ "3. Update address\n"
					+ "4. Update phone\n"
					+ "0. Exit\n");
			switch(Application.getIndex(in, 0, 4, false)) {
			case 0:
				currentBranch = null;
				currentBranchId = null;
				break SUBLOOP;
			case 1:
				checkOut(in);
				break;
			case 2:
				checkIn(in);
				break;
			case 3:
				changeAddress(in);
				break;
			case 4:
				changePhoneNo(in);
				break;
			}
		}
		
	}

	private void checkOut(Scanner in) {
		System.out.println("Which book will you be checking out?");
		
		int currentIndex = 0;
		int selection;
		
		SUBLOOP:
		while(true) {
			String[] results = scrubBooksWithoutCopies(
					dbf.searchAndJoinBooksAndCopies(currentBranchId));
			System.out.println("\nTitle\t\tNo. of Copies Available\n-----------------------------------------");
			int listingsShown = Application.showPartOfList(results, currentIndex, 7, 1, 2);
			System.out.println();
			if(currentIndex > 0) System.out.println("8. Previous");
			if(currentIndex < results.length-7) System.out.println("9. Next");
			System.out.println("0. Exit");
			
			selection = Application.getIndex(in, 0, listingsShown, true);
			if(selection <= 7) {
				if(selection == 0) break SUBLOOP;
				else if(selection+currentIndex-1 < results.length) {
					checkOut(in, results[selection+currentIndex-1]);
							
				}
			}
			else if(selection == 8 && currentIndex > 0) {
				currentIndex -= 7;
				continue;
			}
			else if(selection == 9 && currentIndex < results.length-7) {
				currentIndex += 7;
				continue;
			}
		} intro(in);
		
	}
	
	/**
	 * [bookId, title, noOfCopies]
	 * 
	 * @param in
	 * @param result
	 */
	private void checkOut(Scanner in, String result) {
		String[] bookData = result.split(",");
		
		System.out.println("Would you like to check out " + bookData[1] + "?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			String[] outAndDueDate = dbf.timeNowTimeDue(3);
			
			dbf.setTable("tbl_book_copies");
			
			if(dbf.changeRequest("bookId", Integer.parseInt(bookData[0]),
				"branchId", Integer.parseInt(currentBranchId),
					"noOfCopies", Integer.toString(Integer.parseInt(bookData[2])-1))) {
				dbf.setTable("tbl_book_loans");
				if(dbf.pushRequest(bookData[0], currentBranchId, cardNo, "'"+outAndDueDate[0]+"'", "'"+outAndDueDate[1]+"'")) {
					System.out.println("Checkout successful!\n"
							+ "Your book is due to be returned on: " + outAndDueDate[1]);
					System.out.println("Returning to the directory...");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) { //Didn't want to put a prompt like "Press enter to continue" or something. Allows the user to view their due date.
						e.printStackTrace();
					}
				}
			}
		} dbf.setTable("tbl_borrower");
	}
	
	private String[] scrubBooksWithoutCopies(String[] results) { 
		int notEmpty = 0;
		boolean[] map = new boolean[results.length];
		for(int i = 0; i < results.length; i++) {
			if(!results[i].split(",")[2].equals("0")) {
				notEmpty++;
				map[i] = true;
			}
		} 
		String[] scrubbedResults = new String[notEmpty];
		for(int i = 0; i < results.length; i++) {
			if(map[i]) {
				scrubbedResults[scrubbedResults.length-notEmpty--] = results[i];
			}
		} return scrubbedResults;
	}
	
	/**
	 * [bookId, title, dueDate]
	 * 
	 * @param in
	 */
	private void checkIn(Scanner in) {
		System.out.println("Which book will you be returning?");
		
		int currentIndex = 0;
		int selection;

		SUBLOOP: 
		while (true) {
			String[] results = dbf.getLoanedBooks(currentBranchId, cardNo);
			System.out.println("\nTitle\t\tDue Date\n------------------------------");
			int listingsShown = Application.showPartOfList(results, currentIndex, 7, 1, 2);
			System.out.println();
			if (currentIndex > 0)
				System.out.println("8. Previous");
			if (currentIndex < results.length - 7)
				System.out.println("9. Next");
			System.out.println("0. Exit");
			selection = Application.getIndex(in, 0, listingsShown, true);
			if (selection <= 7) {
				if (selection == 0)
					break SUBLOOP;
				else if (selection + currentIndex - 1 < results.length) {
					checkIn(in, results[selection + currentIndex - 1].split(","));
				}
			} else if (selection == 8 && currentIndex > 0) {
				currentIndex -= 7;
				continue;
			} else if (selection == 9 && currentIndex < results.length - 7) {
				currentIndex += 7;
				continue;
			}
		}
	}
	
	private void checkIn(Scanner in, String[] result) {
		System.out.println("Are you ready to return " + result[1] + "?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			dbf.setTable("tbl_book_loans");
			if(dbf.pullRequest(Integer.parseInt(cardNo), Integer.parseInt(currentBranchId), Integer.parseInt(result[0]))) {
				System.out.println("Check in complete!\n");
				dbf.setTable("tbl_book_copies");
				if(!dbf.changeRequest("bookId", Integer.parseInt(result[0]),
						"branchId", Integer.parseInt(currentBranchId),
							"noOfCopies", Integer.toString(Integer.parseInt(dbf.search("bookId", result[0], "branchId", currentBranchId)[0].split(",")[2])+1))) {
					IssueWarning.code(Warning.SQL_ERROR);
				}
			}
			break;
		case 2:
			break;
		}
	}
	
	private void changeAddress(Scanner in) {
		String[] clientData = dbf.search("cardNo", cardNo)[0].split(",");
		System.out.println("Your current address on file is\n\t" + clientData[2]);
		System.out.println("Would you like to update your address?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			changeAddress(in, clientData);
			break;
		case 2:
			break;
		}
		
		
	}
	
	private void changeAddress(Scanner in, String[] clientData) {
		Application.fixTheScanner(in);
		System.out.println("Please enter your updated address:");
		String newAddress = in.nextLine().trim();
		System.out.println("You entered [" + newAddress + "] as your new address.\n\n"
				+ "Is this correct?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			if(dbf.changeRequest("cardNo", Integer.parseInt(cardNo), "address", newAddress)) {
				System.out.println("Address successfully updated!");
			} else IssueWarning.code(Warning.SQL_ERROR);
			break;
		case 2:
			System.out.println("Would you like to re-enter your updated address?\n"
					+ "1. Yes\n"
					+ "2. No\n");
			switch(Application.getIndex(in, 1, 2, false)) {
			case 1:
				changeAddress(in, clientData);
				break;
			case 2:
				System.out.println("Changes discarded.\nReturning to menu.");
				break;
			}
		}
	}

	private void changePhoneNo(Scanner in) {
		String[] clientData = dbf.search("cardNo", cardNo)[0].split(",");
		System.out.println("Your current phone number on file is\n\t" + clientData[3]);
		System.out.println("Would you like to update your phone number?\n"
				+ "1. Yes\n"
				+ "2. No\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			changePhoneNo(in, clientData);
			break;
		case 2:
			break;
		}
		
		
	}

	private void changePhoneNo(Scanner in, String[] clientData) {
		Application.fixTheScanner(in);
		System.out.println("Please enter your updated phone number:");
		String newNumber = parseNumber(in.nextLine().trim());
		if(isNotValidNumber(newNumber)) {
			System.out.println("The number you entered is not a valid 10-digit phone number.\n"
					+ "If you believe this is a mistake, please confirm your number and try again later, or assistant for help.");
			return;
		}
		newNumber = formatNumber(newNumber);
		System.out.println("You entered [" + newNumber + "] as your new phone number.\n\n" + "Is this correct?\n"
				+ "1. Yes\n" 
				+ "2. No\n");
		switch (Application.getIndex(in, 1, 2, false)) {
		case 1:
			if (dbf.changeRequest("cardNo", Integer.parseInt(cardNo), "phone", newNumber)) {
				System.out.println("Phone number successfully updated!");
			} else
				IssueWarning.code(Warning.SQL_ERROR);
			break;
		case 2:
			System.out.println("Would you like to re-enter your phone number?\n" 
					+ "1. Yes\n" 
					+ "2. No\n");
			switch (Application.getIndex(in, 1, 2, false)) {
			case 1:
				changePhoneNo(in, clientData);
				break;
			case 2:
				System.out.println("Changes discarded.\nReturning to menu.");
				break;
			}
		}
	}

	/**
	 * Regex to format into the common format in the SQL database
	 * @param newNumber
	 * @return
	 */
	private static String formatNumber(String newNumber) {
		return newNumber.replaceFirst("(\\d{3})(\\d{7})", "($1) $2");
	}

	/**
	 * Regex validating that all characters are digits
	 * 
	 * @param newNumber
	 * @return
	 */
	private boolean isNotValidNumber(String newNumber) {
		if(newNumber.length()!=10) return true;
		for(Character c : newNumber.toCharArray()) {
			if(!c.toString().matches("\\d")) return true;
		}
		return false;
	}

	/**
	 * Regex a normal number, probably, out of the input
	 * @param trim
	 * @return
	 */
	private String parseNumber(String trim) {
		String parsed = trim.replaceAll("^(\\+\\d)|^(#\\d)|[-#./\\() +]", "");
		if(parsed.length() > 10) parsed = parsed.replaceAll("^1?", "");
		return parsed;
	}
}
