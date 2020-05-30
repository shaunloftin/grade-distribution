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

/**
 * Helper class that creates a Map of class names with their unabbreviated form,
 * and an ArrayList of Map entries that store the School name in the Key and a
 * list of departments in that school in the value.
 * 
 * @author Landen Master
 * @author Shaun Loftin
 *
 */
public class ClassLists {
	
	public static final String NAME_LIST = "data/Unabbreviated_Names.csv"; //CSV file of abbreviated names in the first column and their full names in the second column
	public static final String DEPARTMENT_LIST = "data/List_of_Departments.xlsx"; //Excel file of school names in the first column and all departments in that school in subsequent columns 
	
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
	
	/**
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String args[]) throws FileNotFoundException, IOException {
		ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>> departmentByCollege = departmentByCollege();
		HashMap<String, String> unabbreviatedClasses = unabbreviatedClasses();
		String currentDepartment;
		for (int i = 0 ; i < departmentByCollege.size(); i++) {
			AbstractMap.SimpleEntry<String, ArrayList<String>> currentSchool = departmentByCollege.get(i);
			ArrayList<String> departments = currentSchool.getValue();
			System.out.println(currentSchool.getKey() + "(" + departments.size() +"): ");
			for (int k = 0; k < departments.size(); k++) {
				currentDepartment = unabbreviatedClasses.get(departments.get(k));
				System.out.print(currentDepartment + ", ");
			}
			System.out.println("\n");
		}
	}
	
	
	/**
	 * Helper function that stores a map of all departments and what college they fall under.
	 * @return
	 * 			Map with keys of abbreviated classes and values of what college they fall under.
	 */
	public static final ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>> departmentByCollege() throws FileNotFoundException, IOException {
		XSSFWorkbook excelBook = new XSSFWorkbook(new FileInputStream(DEPARTMENT_LIST)); // Copying the excel workbook into a local variable
        XSSFSheet mainSheet = excelBook.getSheet("Sheet1"); // Copying the first excel sheet into a local variable
        XSSFRow currentRow; //current row being read in of school and departments
        
		ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>> departmentByCollege = new ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>>(); // ArrayList that stores each School and each of their departments
		String currentCollege; //current college being read in

		//going through each row and getting all departments
		for (int i = 0; i < mainSheet.getPhysicalNumberOfRows(); i++) {
			
			currentRow = mainSheet.getRow(i); //getting current row to read in
			ArrayList<String> currentSchoolDepartments = new ArrayList<String>(); //all departments of current college being read in
			currentCollege = currentRow.getCell(0).toString(); //Official college name being read in
			
			//reading in each abbreviated department name until there are no more departments in the row
			for (int k = 1; k < currentRow.getLastCellNum(); k++) {
				currentSchoolDepartments.add(currentRow.getCell(k).toString());
			}
			
			//creating a Map entry to store the college and its departments
			AbstractMap.SimpleEntry<String, ArrayList<String>> currentSchoolAndDepartments = new AbstractMap.SimpleEntry<String, ArrayList<String>>(currentCollege, currentSchoolDepartments);
			
			//adding current school with its department to the final list
			departmentByCollege.add(currentSchoolAndDepartments);
		}
		
		excelBook.close(); //closing the instream of the excel book
		
		return departmentByCollege; //returning an ArrayList of Map entrys of School names and all abbreviated department names
	}

	
}
