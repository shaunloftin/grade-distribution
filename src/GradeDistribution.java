import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * This program takes an inputed excel sheet of grade distributions over multiple semesters and outputs the pass rate of each class.
 * 
 * @author Shaun Loftin
 * @author Landen Master
 *
 */

public class GradeDistribution {

	/**
	 * Main method that takes an inputed excel sheet of grade distributions and outputs the pass rate of each class
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		 XSSFWorkbook gradeBook = readFromExcel("data/OSU_Grade_Distributions_SU18_AU19.xlsx"); //reads current grade distribution excel sheet
		 
		 gradeBook.close(); //closing local copy of the excel sheet
    }
    
	/**
	 * TODO
	 * @param file
	 * 			excel sheet containing list of courses and grades in a specified format
	 * @return
	 * 			local excel book with all information
	 */
    public static XSSFWorkbook readFromExcel(String file) throws IOException{
        XSSFWorkbook gradeBook = new XSSFWorkbook(new FileInputStream(file)); //Copying the excel workbook into a local variable
        XSSFSheet myExcelSheet = gradeBook.getSheet("Sheet1"); //Copying the first excel sheet into a local variable
        XSSFRow currentRow; //Tracks the current row being read
        HashMap<String, Double> classWithPassRate = new HashMap<String, Double>(); //Map that stores course information and pass rate
     
        /**
         * Explanation of what each column in the excel sheet represents:
         * 
         * COLUMN 0 - Term Semester (ex. AU 2018)
         * COLUMN 1 - Term Code (ex. 1188)
         * COLUMN 2 - Subject Code (ex. CSE)
         * COLUMN 3 - Catalog Number (ex. 2221)
         * COLUMN 4 - # of students with A
         * COLUMN 5 - # of students with A-
         * COLUMN 6 - # of students with B+
         * COLUMN 7 - # of students with B
         * COLUMN 8 - # of students with B-
         * COLUMN 9 - # of students with C+
         * COLUMN 10 - # of students with C
         * COLUMN 11 - # of students with C-
         * COLUMN 12 - # of students with D+
         * COLUMN 13 - # of students with D
         * COLUMN 14 - # of students with E
         * COLUMN 15 - # of students with PA
         * COLUMN 16 - # of students with NP
         * COLUMN 17 - # of students with S
         * COLUMN 18 - # of students with U
         * COLUMN 19 - Total # of students enrolled in the class 
         */
        
        
        //Loop that searches specific course information
        /*for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
        	currentRow = myExcelSheet.getRow(i);
        	if(currentRow.getCell(2).toString().equals("CHEM") 
        			&& currentRow.getCell(3).toString().equals("1250")){
                System.out.println(currentRow.getCell(0) + " Pass Rate: " + calcPassRate(currentRow) + " %");
            }
        }*/
                
        //Reads information on each class and adds each course to a Map that stores the course information and its pass rate
        for (int i = 1; i < myExcelSheet.getLastRowNum(); i++) {
        	currentRow = myExcelSheet.getRow(i); //gets current class at specified semester
        	
        	//TODO
        	if (currentRow.getCell(17) != null && currentRow.getCell(18) != null) {
        		
        		//Combines the Semester, department, and course number into one single course information
        		String courseInformation = currentRow.getCell(0).toString() + " " + currentRow.getCell(2).toString() + " " 
            			+ currentRow.getCell(3).toString();
        		
            	classWithPassRate.put(courseInformation, findPassRate(currentRow)); //Stores course information with its pass rate
        	}
        }
        
        List<Entry<String, Double>> classRankByPassRate = entriesSortedByValues(classWithPassRate); //Stores a sorted list of each class based on its pass rate
        int k = 0; //TODO
        
        //TODO
        for (Map.Entry<String, Double> x : classRankByPassRate) {
        	if (k < 50) { //TODO
        		System.out.println(x.getKey() + " || Pass Rate: " + x.getValue());
        	} else { //TODO
        		break;
        	}
        }
        
        
        return gradeBook;
    }
    
    /**
     * Calculates the pass rate of a given class
     * @param currentCourse
     * 			current class being evaluates
     * @return
     * 			The class passing rate, in percent
     */
    private static double findPassRate(XSSFRow currentCourse) throws IOException {
    	double totalEnrolled = parseDoubleNull(currentCourse.getCell(19).toString()); //finds the total amount of students enrolled in the current course
    	
    	//finds the total amount of students that failed the course in order to find the number of students who passed the course
    	double failedStudents = parseDoubleNull(currentCourse.getCell(12).toString()) + 
    			parseDoubleNull(currentCourse.getCell(13).toString()) + parseDoubleNull(currentCourse.getCell(14).toString());
    	
    	double passRate = (totalEnrolled - failedStudents)/totalEnrolled; //calculates the number of students that passed in the course
    	
		return 100*passRate; //returns the percentage of students that passed the class
    }
    
    /**
     * Parsing function for doubles that returns a 0 if a null value is found
     * @param inputtedNumber
     * 			The String to Parse
     * @return
     * 			[x as a double] || 0
     */
    private static Double parseDoubleNull(String inputtedNumber) {
    	
    	//if the inputed number is an empty string or Null, returns a 0
    	if (inputtedNumber.equals(null) || inputtedNumber.equals("") || inputtedNumber.equals(" ")) {
    		return 0.0;
    		
    		//if the inputed number is not null, returns the parsed value
    	} else {
    		return Double.parseDouble(inputtedNumber);
    	}
    }
    
    /**
     * Helper function to sort sets based on their values
     * @param map
     * 			inputted map whose values must be ordered from greatest to least
     * @return
     * 			List of Entries sorted from greatest values to least values
     */
    static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
    	//TODO: comment this
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		            return e2.getValue().compareTo(e1.getValue());
		        }
		    }
		);
		
		return sortedEntries;
	}

}