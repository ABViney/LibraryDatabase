package com.vin.DataManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Input one or more files of values, designate columns
 * direct output file, ordering types.
 * Best use of this program is to merge foreign keys into dependent tables.
 * 
 * @author viney
 *
 */
public class SQLTableGenerator { //TODO rewrite documentation when not tired
	
	/**
	 * Nested HashMap linking origin file, column labels, and value sets.
	 */
	private Map<String, Map<String, ArrayList<String>>> dataChoice;
	private List<String[]> outputSet;
	private List<Integer> primaryKeys;
	
	/**
	 * Specified delimiter for in and out
	 */
	private String delimiter = ",";
	
	private int rowCount;
	
	/**
	 * Default constructor
	 */
	protected SQLTableGenerator() {
		dataChoice = new HashMap<>();
		outputSet = new ArrayList<String[]>();
		primaryKeys = new ArrayList<>();
		rowCount = 51;
	}
	
	/**
	 * Constructor that reassigns delimiter
	 * @param delimiter
	 */
	protected SQLTableGenerator(String delimiter) {
		this();
		this.delimiter = delimiter;
	}
	
	/**
	 * This value is referenced for both input reading and output formatting.
	 * This method should only be used if the file types being read from differ--
	 * or if a specific delimiter is desired for output files-- from CSV standard.
	 * 
	 * @param delimiter
	 */
	protected void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * Specify the amount of rows containing data values to be returned.
	 * 
	 * @param newRowCount
	 */
	protected void setRowCount(int newRowCount) {
		rowCount = newRowCount+1;
	}
	
	/** 
	 * Reads the file described and generates a key for the file name.
	 * The file name can be used to access subsequent columns and values listed
	 * in that file. 
	 * 
	 * @param fileName
	 */
	protected void importValues(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			dataChoice.put(fileName, new HashMap<>());
			
			String[] headers = br.readLine().trim().split(delimiter);
			for(String header : headers) dataChoice.get(fileName).put(header, new ArrayList<>());
			
			String line = null;
			while((line = br.readLine()) != null) {
				String[] values = line.trim().split(delimiter, headers.length); //Quick fix since books titles have commas to my dismay
				for(int i = 0; i < headers.length; i++) {
					dataChoice.get(fileName).get(headers[i]).add(values[i]);
				}
			}
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Quality of life overload.
	 * @param fileNames varargs amount of files to import
	 */
	protected void importValues(String... fileNames) {
		for(String fileName : fileNames) importValues(fileName);
	}
	
	/**
	 * Push to output list
	 * @param fileName
	 * @param column
	 */
	protected void pushToOutSet(String fileName, String column) {
		ArrayList<String> currentSet = new ArrayList<>(Arrays.asList(column));
		currentSet.addAll(dataChoice.get(fileName).get(column));
		outputSet.add(currentSet.toArray(new String[currentSet.size()]));
	}
	
	/**
	 * Reassigns the output set for a new export.
	 */
	protected void clearOutSet() {
		outputSet = new ArrayList<String[]>();
	}
	
	/**
	 * Since not all columns will have the same number of arguments, a boolean tracker
	 * ascertains whether all values of a set have been placed at least once, after which
	 * values will be pulled according to #Random to fill in the remainder. 
	 * @param tableName
	 */
	protected void exportNewTable(String tableName) {
		File newTable = new File(tableName);
		try(PrintWriter pw = new PrintWriter(newTable)) {
			newTable.createNewFile();
			String outputToPrint = condenseSetToString();
			pw.write(outputToPrint);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private String condenseSetToString() {
		StringBuilder output = new StringBuilder();
		String[][] rawSet = formatOutputToRowCount();
		for(int row = 0; row < rowCount; row++) {
			for(int col = 0; col < rawSet.length; col++) {
				output.append(rawSet[col][row] + (col != rawSet.length-1 ? "," : "\n"));
			}
		} return output.toString();
	}
	
	/**
	 * Resizes output to match {@code rowCount} and returns a 2d array
	 * for printing.
	 * 
	 * @return String[][]
	 */
	private String[][] formatOutputToRowCount() {
		String[][] outputSizeArray = new String[outputSet.size()][rowCount];
		
		for(int col = 0; col < outputSet.size(); col++) {
			String[] arraySet = Arrays.copyOf(outputSet.get(col), rowCount);
			for(int row = 0; row < rowCount; row++) {
				if(arraySet[row] == null) upSize(arraySet, row);
				outputSizeArray[col][row] = arraySet[row];
			}
		} return outputSizeArray;
	}

	/**
	 * Fills the remainder of a column with randomly picked non-null values.
	 * 
	 * @param arraySet array being modified
	 * @param firstNull first index to return null
	 */
	private void upSize(String[] arraySet, int firstNull) {
		Random r = new Random();
		for(int row = firstNull; row < arraySet.length; row++) 
			arraySet[row] = arraySet[r.nextInt(firstNull)];
	}

}
