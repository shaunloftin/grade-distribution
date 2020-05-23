import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		unabbreviatedClasses();
	}
	
	
	/**
	 * Helper function that stores a map of all departments and what college they fall under.
	 * @return
	 * 			Map with keys of abbreviated classes and values of what college they fall under.
	 */
	public static final ArrayList<Map.Entry<String, ArrayList<String>>> departmentByCollege() throws FileNotFoundException, IOException {
		ArrayList<Map.Entry<String, ArrayList<String>>> departmentByCollege = new ArrayList<Map.Entry<String, ArrayList<String>>>(); //ArrayList that stores each School and each of their departments
		//TOOD: read in excel file
		String currentSchool; //current school being read
		ArrayList<String> currentSchoolDepartments = new ArrayList<String>(); //all departments in current school
		
		//TODO implement this loop
		/*
		 * for (int i = 0; i < excel rows; i++) {
		 * 		currentSchoolDepartments.clear();
		 * 		currentSchool = first cell of current row;
		 * 		while (!next cell is empty) {
		 * 			currentSchoolDepartments.put(current cell);
		 * 		}
		 * }
		 */
		
		return departmentByCollege;
	}

	
}
