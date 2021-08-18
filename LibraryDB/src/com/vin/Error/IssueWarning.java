package com.vin.Error;

import java.util.HashMap;

public class IssueWarning {
	
	private static final HashMap<Warning, VoidFI> issue = new HashMap<>();
	static {

		issue.put(Warning.BAD_INPUT, () -> System.out.println("The input could not be parsed accurately. Ensure your query is typed correctly."));
		issue.put(Warning.ILLEGAL_ARGUMENT, () -> System.out.println("That action cannot be performed."));
		issue.put(Warning.NO_TARGET, () -> System.out.println("Please make a selection from the list."));
		issue.put(Warning.SQL_ERROR, () -> System.out.println("Error occured while interacting with the database."));
		issue.put(Warning.INCORRECT_ARGUMENTS, () -> System.out.println("Incorrect number or type of arguments provided."));
		
	}
	
	public static void code(Warning key) {
		issue.getOrDefault(key, () -> System.out.println("Error: Key does not exist.")).describe();;
	}
}
