package com.vin.LibMgmt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import com.vin.Error.IssueWarning;
import com.vin.Error.Warning;
import com.vin.JDBC.DBFacet;

/**
 * Administrator interface to permit a more direct means of mutating information.
 * 
 * @author viney
 *
 */
public class Administrator {
	
	private final boolean authorizedUser;
	private DBFacet dbf;
	
	public Administrator(Scanner in) {
		authorizedUser = verifyAdmin(in);
	}
	
	private boolean verifyAdmin(Scanner in) {
		System.out.print("Enter authentication passcode: ");
		Application.fixTheScanner(in);
		try(BufferedReader br = new BufferedReader(new FileReader("resources/admin.txt"))) {
			if(br.readLine().trim().equals(in.nextLine().trim())) return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("Permissions denied.\n");
		return false;
	}
	
	public void intro(Scanner in) {
		if(!authorizedUser) return;
		dbf = new DBFacet();
		System.out.println("AuthKey Verified\n");
		
		SUBLOOP:
		while(true) {
			System.out.println("1. Add new records\n"
					+ "2. Edit existing records\n"
					+ "3. Move books from one branch to another\n"
					+ "0. Exit");
			switch(Application.getIndex(in, 0, 2, false)) {
			case 0:
				break SUBLOOP;
			case 1:
				addRecords(in);
				break;
			case 2:
				editRecords(in);
				break;
			case 3:
				shiftBookStock(in);
				break;
			}
		}
	}
	
	/**
	 * Borrower
	 * Library branch
	 * Book - AND - Book copies - AND - branch
	 * @param in
	 */
	private void addRecords(Scanner in) {
		SUBLOOP:
			while(true) {
				System.out.println("1. Add new borrower\n"
						+ "2. Add new book\n"
						+ "3. Add new library branch info\n"
						+ "0. Return to previous");
				switch(Application.getIndex(in, 0, 3, false)) {
				case 0:
					break SUBLOOP;
				case 1:
					//addBorrower(in);
					break;
				case 2:
					//addBook(in);
					break;
				case 3:
					//addBranch(in);
					break;
				}
			}
	}
	
	/**
	 * Borrower
	 * Library Branch
	 * Book loans
	 * 
	 * @param in
	 */
	private void editRecords(Scanner in) {
		SUBLOOP:
		while(true) {
			System.out.println("1. Edit Borrower info\n"
					+ "2. Edit Book info\n"
					+ "3. Edit Library Branch info\n"
					+ "4. View current book loans\n"
					+ "0. Return to previous");
			switch(Application.getIndex(in, 0, 3, false)) {
			case 0:
				break SUBLOOP;
			case 1:
				editBorrowers(in);
				break;
			case 2:
				//editBooks(in);
				break;
			case 3:
				//editLibraries(in);
				break;
			case 4:
				//viewBookLoans(in);
				break;
			}
		}
	}
	
	/**
	 * Allow deleting borrowers and viewing current bookLoans
	 * 
	 * @param in
	 */
	private void editBorrowers(Scanner in) {
		String[] cardHolderInfo = null;
		SUBLOOP:
		while(true) {
			dbf.setTable("tbl_borrower");
			if(cardHolderInfo != null)
				System.out.println("Borrower selected: \t" + cardHolderInfo[0] + " : " + cardHolderInfo[1] +"\n");
			else System.out.println("No record selected.");
			
			System.out.println("1. Select new Borrower");
			if(cardHolderInfo != null) System.out.println("2. View Borrower's loans");
			if(cardHolderInfo != null) System.out.println("3. Delete Borrower");
			System.out.println("0. Return to previous");
			
			int selection = Application.getIndex(in, 0, 3, false);
			if(selection == 0) break SUBLOOP;
			else if(selection == 1) {
				String[] result = selectCardHolder(in);
				if(result != null) cardHolderInfo = result;
			}
			else if(cardHolderInfo != null && selection == 2) {
				displayBorrowerLoans(in, cardHolderInfo);
			}
			else if(cardHolderInfo != null && selection == 3) {
				if(deleteBorrower(in, cardHolderInfo)) {
					System.out.println("Record for " + cardHolderInfo[0] + " deleted.");
					cardHolderInfo = null;
				}
			}
		}
	}
	
	private String[] selectCardHolder(Scanner in) {
		System.out.println("Enter a borrower's ID to continue");
		int cardNo = Application.getIndex(in, 1, Integer.MAX_VALUE, false);
		String[] borrowerInfo = dbf.searchExact("cardNo", Integer.toString(cardNo))[0].split(",");
		if(borrowerInfo.length == 4) return borrowerInfo;
		return null;
	}
	
	private void displayBorrowerLoans(Scanner in, String[] cardHolder) {
		dbf.setTable("tbl_book_loans");
		System.out.println("Current loans for: " + cardHolder[1]);
		String[] results = dbf.searchExact("cardNo", cardHolder[0]);
		
		int currentIndex = 0;
		int selection = 0;
		SUBLOOP:
		while(true) {
			int listingsShown = Application.showPartOfList(results, currentIndex, 7, 0,1,2,3,4);
			System.out.println();
			if(currentIndex > 0) System.out.println("8. Previous");
			if(currentIndex < results.length-7) System.out.println("9. Next");
			System.out.println("0. Return to previous.");
			
			selection = Application.getIndex(in, 0, listingsShown, true);
			if(selection == 0) break SUBLOOP;
			else if(selection == 8 && currentIndex > 0) {
				currentIndex -= 7;
				continue;
			}
			else if(selection == 9 && currentIndex < results.length-7) {
				currentIndex += 7;
				continue;
			}
		}
	}
	
	private boolean deleteBorrower(Scanner in, String[] cardHolder) {
		System.out.println("Are you sure you want to delete the record for:\n"
				+ cardHolder[0] + "\t"+cardHolder[1] + "?\n"
						+ "1. Yes\n"
						+ "2. No.\n");
		switch(Application.getIndex(in, 1, 2, false)) {
		case 1:
			if(dbf.adminDeleteByKey("cardNo", cardHolder[0])) {
				return true;
			} else IssueWarning.code(Warning.SQL_ERROR);
		} return false;
	}
	
	/**
	 * Book copies
	 * 
	 * @param in
	 */
	private void shiftBookStock(Scanner in) {
		
	}
}
