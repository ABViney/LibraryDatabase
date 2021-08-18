package com.vin.LibMgmt;
import java.util.Scanner;

import com.vin.Error.IssueWarning;
import com.vin.Error.Warning;

public class Application {
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		start(in);
		in.close();
	}
	
	public static void start(Scanner in) {
		MAINLOOP:
		while (true) {
			System.out.println("Welcome to the GCIT Library Management System. Which category of user are you?");
			System.out.println("1. Librarian\n" + "2. Administrator\n" + "3. Borrower\n" + "0. Exit\n");

			switch (Application.getIndex(in, 0, 3, false)) {
			case 0:
				break MAINLOOP;
			case 1:
				new Librarian().intro(in);
				break;
			case 2:
				new Administrator(in).intro(in);
				break;
			case 3:
				new Borrower().verifyID(in);
				break;
			}
		}
	}
	
	/**
	 * Grabs input from the user ensuring that it is within the bounds of accepted values.
	 * 
	 * @param in
	 * @param min
	 * @param max
	 * @param reserve if true 8 and 9 are set to reserved
	 * @return
	 */
	public static int getIndex(Scanner in, int min, int max, boolean reserve) {
		while(true) {
			while(!in.hasNextInt()) {
				IssueWarning.code(Warning.BAD_INPUT);
				in.next();
			} int selection = in.nextInt();
			if(reserve) {
				if(selection >= min || selection <= max ||
						selection == 8 || selection == 9) return selection;
			}
			if(selection >= min || selection <= max) return selection; 
			IssueWarning.code(Warning.NO_TARGET);
		}
	}
	
	public static void fixTheScanner(Scanner in) {
		in.skip("(\r\n|[\n\r\u2028\u2029\u0085])?"); //Skip every escape character
	}
	
	/**
	 * Display a set of results from the passed array.
	 * Notifies the caller of how many results are presented.
	 * 
	 * @param results String array of display values
	 * @param start Index to begin display from
	 * @param max Number of results to display
	 * @return the number of results displayed
	 */
	public static int showPartOfList(String[] results, int start, int max, int... columns) {
		if(results == null) {
			System.out.println("Nothing to display.");
			return 0;
		}
			if(start+max > results.length) max = results.length - start;
			if(results[0].length() <= 1) return 0;
			for(int i = 1; i <= max; i++) {
				System.out.println(i + ". " + formatToCells(results[start+i-1], columns));
			} System.out.println("\t" + (start+max) + " of " + results.length); 
			return max;
		}
	
	/**
	 * Takes a String and returns a String of split information. Used for formatting.
	 * 
	 * @param result
	 * @param columns to display [0 for branch ID, 1 for branch name]
	 * @return
	 */
	private static String formatToCells(String result, int... columns) {
		String output = "";
		String[] split = result.split(",");
		for(int i = 0; i < columns.length; i++) {
			output += split[columns[i]] + "    ";
		} return output;
	}
	
	public static String[] pickFromList(Scanner in, String[] results, int... columnsToDisplay) {
		int currentIndex = 0;
		int selection = 0;

		while (true) {
			int listingsShown = Application.showPartOfList(results, currentIndex, 7, columnsToDisplay);
			System.out.println();
			if (currentIndex > 0)
				System.out.println("8. Previous");
			if (currentIndex < results.length - 7)
				System.out.println("9. Next");
			System.out.println("0. Return to previous.");

			selection = Application.getIndex(in, 0, listingsShown, true);
			if (selection <= 7) {
				if (selection == 0)
					break;
				else if (selection + currentIndex - 1 < results.length) {
					return results[selection + currentIndex - 1].split(",");
				}
			} else if (selection == 8 && currentIndex > 0) {
				currentIndex -= 7;
				continue;
			} else if (selection == 9 && currentIndex < results.length - 7) {
				currentIndex += 7;
				continue;
			}
		} return null;
	}

}
