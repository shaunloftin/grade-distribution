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
		ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>> departmentByCollege = departmentByCollege();
		HashMap<String, String> unabbreviatedClasses = unabbreviatedClasses();
		String currentDepartment;
		for (int i = 0 ; i < departmentByCollege.size(); i++) {
			AbstractMap.SimpleEntry<String, ArrayList<String>> currentSchool = departmentByCollege.get(i);
			System.out.println(currentSchool.getKey() + ": ");
			ArrayList<String> departments = currentSchool.getValue();
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
        XSSFRow currentRow;
        
		ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>> departmentByCollege = new ArrayList<AbstractMap.SimpleEntry<String, ArrayList<String>>>(); // ArrayList that stores each School and each of their departments
		String currentCollege; //curent college being read in

		System.out.println(mainSheet.getPhysicalNumberOfRows());
		
		for (int i = 0; i < mainSheet.getPhysicalNumberOfRows(); i++) {
			currentRow = mainSheet.getRow(i);
			ArrayList<String> currentSchoolDepartments = new ArrayList<String>(); //all departments of current college being read in
			currentCollege = currentRow.getCell(0).toString();
			for (int k = 1; k < currentRow.getLastCellNum(); k++) {
				currentSchoolDepartments.add(currentRow.getCell(k).toString());
			}
			AbstractMap.SimpleEntry<String, ArrayList<String>> currentSchoolAndDepartments = new AbstractMap.SimpleEntry<String, ArrayList<String>>(currentCollege, currentSchoolDepartments);
			departmentByCollege.add(currentSchoolAndDepartments);
		}
		excelBook.close();
		return departmentByCollege;
	}

	
}
