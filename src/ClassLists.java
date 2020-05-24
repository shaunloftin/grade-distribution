 import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ClassLists {
	
	public static final String NAME_LIST = "data/Unabbreviated_Names.csv";
	public static final String DEPARTMENT_LIST = "data/List_of_Departments.xlsx";
	
	/**
	 * Helper function that stores a map of all departments and their unabbreviated names.
	 * 
	 * @return
	 * 			Map with keys of abbreviated classes and values of their unabbreviated name.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static final HashMap<String, String> unabbreviatedClasses() throws FileNotFoundException, IOException {
		HashMap<String, String> unabbreviatedClasses = new HashMap<String, String>(); // map that stores classes and their unabbreviated name
		BufferedReader nameReader = new BufferedReader(new FileReader(NAME_LIST)); //reads CSV file of each name and their full department list
	    String line = nameReader.readLine(); //current line in CSV file

	    while((line=nameReader.readLine()) != null) {
	    	String str[] = line.split(",", 2);
	    	unabbreviatedClasses.put(str[0], str[1]);
	    }
	    
	    nameReader.close(); //closing reader stream
	    return unabbreviatedClasses; //returning the full map
	}
	
	public static void main(String args[]) throws FileNotFoundException, IOException {
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Type 1 to test unabbreviatedClasses, 2 to test deptByCollege: ");
		String input = keyboard.nextLine();
		
		while (!input.equals("1") && !input.equals("2")) {
			System.err.println("ERROR: Expected 1 or 2.");
			System.out.print("Type 1 to test unabbreviatedClasses, 2 to test deptByCollege: ");
			input = keyboard.nextLine();
		}
		
		if (input.equals("1") ) {
			HashMap<String, String> data = unabbreviatedClasses();
			for (Map.Entry<String, String> entry : data.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		} else {
			ArrayList<Map.Entry<String, ArrayList<String>>> data = departmentByCollege();
			for (Map.Entry<String, ArrayList<String>> college : data) {
				System.out.print(college.getKey() + " - ");
				for (String department : college.getValue()) {
					System.out.print(department + " ");
				}
			}
		}
		
		keyboard.close();
	}
	
	
	/**
	 * Helper function that stores a map of all departments and what college they fall under.
	 * @return
	 * 			Map with keys of abbreviated classes and values of what college they fall under.
	 */
	public static final ArrayList<Map.Entry<String, ArrayList<String>>> departmentByCollege() throws FileNotFoundException, IOException {
		XSSFWorkbook excelBook = new XSSFWorkbook(new FileInputStream(DEPARTMENT_LIST)); // Copying the excel workbook into a local variable
        XSSFSheet mainSheet = excelBook.getSheet("Sheet1"); // Copying the first excel sheet into a local variable
        XSSFRow currentRow;
        
		ArrayList<Map.Entry<String, ArrayList<String>>> departmentByCollege = new ArrayList<Map.Entry<String, ArrayList<String>>>(); // ArrayList that stores each School and each of their departments
		// TODO - read in excel file
		String currentSchool; // current school being read
		ArrayList<String> currentSchoolDepartments = new ArrayList<String>(); // all departments in current school
		
		// TODO - fix bug when reading row with dept of military sci
		/*
		 * for (int i = 0; i < excel rows; i++) {
		 * 		currentSchoolDepartments.clear();
		 * 		currentSchool = first cell of current row;
		 * 		while (!next cell is empty) {
		 * 			currentSchoolDepartments.put(current cell);
		 * 		}
		 * }
		 */
		
		for (int i = 0; i < mainSheet.getLastRowNum(); i++) {
			currentSchoolDepartments.clear();
			currentRow = mainSheet.getRow(i);
			currentSchool = currentRow.getCell(0).toString();
			
			for (int k = 1; k < (int)(currentRow.getLastCellNum())-1; k++) {	
				System.out.println(currentSchool + ", " + currentRow.getCell(k).toString() + " last cell: " + ((int)(currentRow.getLastCellNum())-1));
				currentSchoolDepartments.add(currentRow.getCell(k).toString());
			}
			departmentByCollege.add(new AbstractMap.SimpleEntry<String, ArrayList<String>>(currentSchool, currentSchoolDepartments));
		}
		
		excelBook.close();
		return departmentByCollege;
	}

	
}
