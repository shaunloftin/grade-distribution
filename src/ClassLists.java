import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ClassLists {
	
	public static final String NAME_LIST = "data/Unabbreviated_Names.csv";
	//public static final String DEPARTMENT_LIST = "data/Colleges_and_Departments.csv";
	
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
		BufferedReader br = new BufferedReader(new FileReader(NAME_LIST));
	    String line = br.readLine();

	    while((line=br.readLine()) != null) {
	    	String str[] = line.split(",", 2);
	    	System.out.println(str[0] + "\t\t" + str[1]);
	    	unabbreviatedClasses.put(str[0], str[1]);
	    }
	    
	    System.out.println(unabbreviatedClasses.size());
	        
		/* for (int i = 1; i < listOfNames.size; i++) {
			currentRow = currentRow+1;
			unabbreviatedClasses.put(currentRow.getCell(0), currentRow.getCell(1));
		} */
	    
	    br.close();
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
	public static final HashMap<String, String> classByCollege() throws FileNotFoundException, IOException {
		//TODO: implement once the department list is created
		 HashMap<String, String> classByCollege = new HashMap<String, String>();
		return classByCollege;
	}

	
}
