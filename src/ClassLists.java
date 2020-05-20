import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ClassLists {
	
	public static final String NAME_LIST = "data/Unabbreviated_Names.csv";
	//public static final String DEPARTMENT_LIST = "data/Colleges_and_Departments.csv";
	
	/**
	 * Helper function that stores a map of all departments and their unabbreviated names.
	 * @return
	 * 			Map with keys of abbreviated classes and values of their unabbreviated name.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static final HashMap<String, String> unabbreviatedClasses() throws FileNotFoundException, IOException {
		
		//TODO: READ IN CSV FILE
		//XSSFWorkbook listOfNames = new XSSFWorkbook(new FileInputStream(NAME_LIST)); // Copying the excel workbook into a local variable
		HashMap<String, String> unabbreviatedClasses = new HashMap<String, String>(); //map that stores classes and their unabbreviated name
		 
		/* TODO: implement this loop once the CSV file is read in
		for (int i = 1; i < listOfNames.size; i++) {
			currentRow = currentRow+1;
			unabbreviatedClasses.put(currentRow.getCell(0), currentRow.getCell(1));
		}
		 */
		 return unabbreviatedClasses; //returning the full map
	}
	
	
	/**
	 * Helper function that stores a map of all departments and what college they fall under.
	 * @return
	 * 			Map with keys of abbreviated classes and values of what college they fall under.
	 */
	public static final HashMap<String, String> classByCollege() throws FileNotFoundException, IOException {
		//TODO: implement once the department list is created
		 HashMap<String, String> classByCollege = new HashMap<String, String>();
		return classByCollege;
	}

	
}
